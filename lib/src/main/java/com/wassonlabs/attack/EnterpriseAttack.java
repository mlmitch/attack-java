package com.wassonlabs.attack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides access to the ATT&amp;CK framework.
 */
public final class EnterpriseAttack {

    private static final String ENTERPRISE_ATTACK_FILE_NAME = "enterprise-attack.xml";

    private static final ParsedAttack parsedAttack = AttackParser.parseAttack(ENTERPRISE_ATTACK_FILE_NAME);
    private static final Map<String, Tactic> tactics = createMap(parsedAttack.tactics(), Tactic::id);
    private static final Map<String, Technique> techniques = createMap(parsedAttack.techniques(), Technique::id);
    private static final Map<String, Subtechnique> subtechniques = createMap(parsedAttack.subtechniques(), Subtechnique::id);

    private static <T> Map<String, T> createMap(List<T> list, Function<T, String> idExtractor) {
        Map<String, T> map = new HashMap<>();
        for (T item : list) {
            map.put(idExtractor.apply(item), item);
        }
        return Map.copyOf(map);
    }

    private EnterpriseAttack() {
    }

    /**
     * Retrieves the tactic with a given ID.
     *
     * @param id The ID of the tactic to retrieve.
     * @return The tactic with the given ID, or null if no such tactic exists.
     */
    public static Tactic getTactic(String id) {
        return tactics.get(id);
    }

    /**
     * Retrieves the technique with a given ID.
     *
     * @param id The ID of the technique to retrieve.
     * @return The technique with the given ID, or null if no such technique exists.
     */
    public static Technique getTechnique(String id) {
        return techniques.get(id);
    }

    /**
     * Retrieves the subtechnique with a given ID.
     *
     * @param id The ID of the subtechnique to retrieve.
     * @return The subtechnique with the given ID, or null if no such subtechnique exists.
     */
    public static Subtechnique getSubtechnique(String id) {
        return subtechniques.get(id);
    }
}
