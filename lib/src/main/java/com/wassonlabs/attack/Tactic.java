package com.wassonlabs.attack;

import java.util.List;

/**
 * A class representing a tactic in the ATT&amp;CK framework.
 *
 * @param id The unique identifier for the tactic.
 * @param name The name of the tactic.
 * @param description A description of the tactic.
 * @param techniqueIds The IDs of the techniques that implement this tactic.
 */
public record Tactic(String id, String name, String description, List<String> techniqueIds) {
}
