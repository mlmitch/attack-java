package com.wassonlabs.attack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.*;

final class AttackParser {

    private AttackParser() {
    }

    static ParsedAttack parseAttack(String attackSourceFile) {
        Document attackDocument = parseAttackSourceFile(attackSourceFile);
        Element attackElement = attackDocument.getDocumentElement();
        if (!attackElement.getNodeName().equals("attack")) {
            throw new RuntimeException("Unexpected node name: " + attackElement.getNodeName());
        }
        NodeList attackChildren = attackElement.getChildNodes();

        List<Tactic> tactics = null;
        List<Technique> techniques = null;
        List<Subtechnique> subtechniques = null;

        for (int i = 0; i < attackChildren.getLength(); i++) {
            Node attackChild = attackChildren.item(i);
            String attackChildName = attackChild.getNodeName();
            switch (attackChildName) {
                case "tactics" -> tactics = parseTactics(attackChild);
                case "techniques" -> techniques = parseTechniques(attackChild);
                case "subtechniques" -> subtechniques = parseSubtechniques(attackChild);
                default -> throw new RuntimeException("Unexpected node name: " + attackChild.getNodeName());
            }
        }

        return new ParsedAttack(tactics, techniques, subtechniques);
    }

    private static List<String> parseReferences(Node referencesNode, String referenceNodeName) {
        List<String> references = new ArrayList<>();

        NodeList referencesChildren = referencesNode.getChildNodes();
        for (int i = 0; i < referencesChildren.getLength(); i++) {
            Node referenceNode = referencesChildren.item(i);

            if (!referenceNode.getNodeName().equals(referenceNodeName)) {
                continue;
            }
            String referenceId = referenceNode.getAttributes().getNamedItem("id").getNodeValue();

            references.add(referenceId);
        }

        // These are used directly in the records exposed to library users
        // Everything exposed to library users needs to be immutable
        return List.copyOf(references);
    }

    private static List<Tactic> parseTactics(Node tacticsNode) {
        List<Tactic> tactics = new ArrayList<>();

        NodeList tacticsChildren = tacticsNode.getChildNodes();
        for (int i = 0; i < tacticsChildren.getLength(); i++) {
            Node tacticNode = tacticsChildren.item(i);
            String tacticNodeName = tacticNode.getNodeName();
            if (tacticNodeName.equals("tactic")) {
                String tacticId = tacticNode.getAttributes().getNamedItem("id").getNodeValue();
                String tacticName = tacticNode.getAttributes().getNamedItem("name").getNodeValue();
                String tacticDescription = tacticNode.getAttributes().getNamedItem("description").getNodeValue();
                List<String> techniqueIds = parseReferences(tacticNode, "technique");
                tactics.add(new Tactic(tacticId, tacticName, tacticDescription, techniqueIds));
            } else {
                throw new RuntimeException("Unexpected node name: " + tacticNode.getNodeName());
            }
        }

        return tactics;
    }

    private static List<Technique> parseTechniques(Node techniquesNode) {
        List<Technique> techniques = new ArrayList<>();

        NodeList techniquesChildren = techniquesNode.getChildNodes();
        for (int i = 0; i < techniquesChildren.getLength(); i++) {
            Node techniqueNode = techniquesChildren.item(i);
            String techniqueNodeName = techniqueNode.getNodeName();
            if (techniqueNodeName.equals("technique")) {
                String techniqueId = techniqueNode.getAttributes().getNamedItem("id").getNodeValue();
                String techniqueName = techniqueNode.getAttributes().getNamedItem("name").getNodeValue();
                String techniqueDescription = techniqueNode.getAttributes().getNamedItem("description").getNodeValue();
                List<String> subtechniqueIds = parseReferences(techniqueNode, "subtechnique");
                List<String> tacticIds = parseReferences(techniqueNode, "tactic");

                techniques.add(new Technique(techniqueId, techniqueName, techniqueDescription, tacticIds, subtechniqueIds));
            } else {
                throw new RuntimeException("Unexpected node name: " + techniqueNode.getNodeName());
            }
        }

        return techniques;
    }

    private static List<Subtechnique> parseSubtechniques(Node subtechniquesNode) {
        List<Subtechnique> subtechniques = new ArrayList<>();

        NodeList subtechniquesChildren = subtechniquesNode.getChildNodes();
        for (int i = 0; i < subtechniquesChildren.getLength(); i++) {
            Node subtechniqueNode = subtechniquesChildren.item(i);
            String subtechniqueNodeName = subtechniqueNode.getNodeName();
            if (subtechniqueNodeName.equals("subtechnique")) {
                String subtechniqueId = subtechniqueNode.getAttributes().getNamedItem("id").getNodeValue();
                String subtechniqueName = subtechniqueNode.getAttributes().getNamedItem("name").getNodeValue();
                String subtechniqueDescription = subtechniqueNode.getAttributes().getNamedItem("description").getNodeValue();
                String subtechniqueTechnique = subtechniqueNode.getAttributes().getNamedItem("technique").getNodeValue();

                subtechniques.add(new Subtechnique(subtechniqueId, subtechniqueName, subtechniqueDescription, subtechniqueTechnique));
            } else {
                throw new RuntimeException("Unexpected node name: " + subtechniqueNode.getNodeName());
            }
        }

        return subtechniques;
    }

    private static Document parseAttackSourceFile(String attackSourceFile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // Configuration hardening from https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#jaxp-documentbuilderfactory-saxparserfactory-and-dom4j
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Failed to apply XML parser hardening.", e);
        }

        Document d;
        try (InputStream is = new BufferedInputStream(Objects.requireNonNull(EnterpriseAttack.class.getClassLoader().getResourceAsStream(attackSourceFile)))) {
            d = dbf.newDocumentBuilder().parse(is);
        } catch (Exception e) {
            throw new RuntimeException("Error loading MITRE ATT&CK data.", e);
        }

        return d;
    }
}
