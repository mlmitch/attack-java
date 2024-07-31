package com.wassonlabs.attack;

import java.util.List;

/**
 * A class representing a technique in the ATT&amp;CK framework.
 *
 * @param id The unique identifier for the technique.
 * @param name The name of the technique.
 * @param description A description of the technique.
 * @param tacticIds The IDs of the tactics that this technique implements.
 * @param subtechniqueIds The IDs of the subtechniques that belong to this technique.
 */
public record Technique(String id, String name, String description, List<String> tacticIds, List<String> subtechniqueIds) {
}
