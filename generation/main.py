from mitreattack.stix20 import MitreAttackData
import xml.etree.ElementTree as ET

def get_external_id(mitre_object):
    for reference in mitre_object['external_references']:
        if reference['source_name'] == 'mitre-attack':
            return reference['external_id']

tactics_lookup_by_technique = {}
techniques_lookup_by_tactic = {}
def populate_attack_lookup_tables(attack_data: MitreAttackData):
    for tactic in attack_data.get_tactics():
        # gets both techniques and subtechniques
        all_techniques_for_tactic = attack_data.get_techniques_by_tactic(tactic_shortname=tactic.get_shortname(), domain="enterprise-attack")

        tactic_external_id = get_external_id(tactic)

        techniques_lookup_by_tactic[tactic_external_id] = []

        for technique in all_techniques_for_tactic:
            if "." in get_external_id(technique): # skip subtechniques
                continue

            technique_external_id = get_external_id(technique)
            techniques_lookup_by_tactic[tactic_external_id].append(technique_external_id)

            if technique_external_id not in tactics_lookup_by_technique:
                tactics_lookup_by_technique[technique_external_id] = []

            tactics_lookup_by_technique[technique_external_id].append(tactic_external_id)

def parse_attack(attack_data: MitreAttackData):
    parsed_attack = {
        "tactics": [],
        "techniques": [],
        "subtechniques": []
    }

    for tactic in attack_data.get_tactics():
        tactic_external_id = get_external_id(tactic)
        parsed_tactic = {
            "id": tactic_external_id,
            "name": tactic['name'],
            "description": tactic['description'],
            "techniques": techniques_lookup_by_tactic[tactic_external_id]
        }
        parsed_attack["tactics"].append(parsed_tactic)

    for technique in attack_data.get_techniques(include_subtechniques=False):
        subtechniques = attack_data.get_subtechniques_of_technique(technique_stix_id=technique['id'])
        technique_external_id = get_external_id(technique)
        parsed_technique = {
            "id": technique_external_id,
            "name": technique['name'],
            "description": technique['description'],
            "tactics": tactics_lookup_by_technique[technique_external_id],
            "subtechniques": [get_external_id(st['object']) for st in subtechniques]
        }
        parsed_attack["techniques"].append(parsed_technique)

        for subtechnique in subtechniques:
            subtechnique_object = subtechnique['object']
            subtechnique_external_id = get_external_id(subtechnique_object)
            parsed_subtechnique = {
                "id": subtechnique_external_id,
                "name": subtechnique_object['name'],
                "description": subtechnique_object['description'],
                "technique": technique_external_id
            }
            parsed_attack["subtechniques"].append(parsed_subtechnique)

    return parsed_attack

def export_attack(parsed_attack, output_file):
    root = ET.Element("attack")

    tactics = ET.SubElement(root, "tactics")
    for tactic in parsed_attack["tactics"]:
        tactic_element = ET.SubElement(tactics, "tactic")
        tactic_element.set("id", tactic["id"])
        tactic_element.set("name", tactic["name"])
        tactic_element.set("description", tactic["description"])
        for technique in tactic["techniques"]:
            technique_element = ET.SubElement(tactic_element, "technique")
            technique_element.set("id", technique)

    techniques = ET.SubElement(root, "techniques")
    for technique in parsed_attack["techniques"]:
        technique_element = ET.SubElement(techniques, "technique")
        technique_element.set("id", technique["id"])
        technique_element.set("name", technique["name"])
        technique_element.set("description", technique["description"])
        for tactic in technique["tactics"]:
            tactic_element = ET.SubElement(technique_element, "tactic")
            tactic_element.set("id", tactic)
        for subtechnique in technique["subtechniques"]:
            subtechnique_element = ET.SubElement(technique_element, "subtechnique")
            subtechnique_element.set("id", subtechnique)

    subtechniques = ET.SubElement(root, "subtechniques")
    for subtechnique in parsed_attack["subtechniques"]:
        subtechnique_element = ET.SubElement(subtechniques, "subtechnique")
        subtechnique_element.set("id", subtechnique["id"])
        subtechnique_element.set("name", subtechnique["name"])
        subtechnique_element.set("description", subtechnique["description"])
        subtechnique_element.set("technique", subtechnique["technique"])

    tree = ET.ElementTree(root)
    tree.write(output_file, encoding='utf-8', xml_declaration=True)

def main():
    enterprise_attack_source_file = "generation/cti/enterprise-attack/enterprise-attack.json"
    enterprice_attack_output_file = "data/src/main/resources/enterprise-attack.xml"

    mitre_attack_data = MitreAttackData(enterprise_attack_source_file)

    populate_attack_lookup_tables(mitre_attack_data)

    parsed_attack = parse_attack(mitre_attack_data)

    export_attack(parsed_attack, enterprice_attack_output_file)

if __name__ == "__main__":
    main()
