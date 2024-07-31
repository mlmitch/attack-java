package com.wassonlabs.attack;

/**
 * A class representing a subtechnique in the ATT&amp;CK framework.
 *
 * @param id The unique identifier for the subtechnique.
 * @param name The name of the subtechnique.
 * @param description A description of the subtechnique.
 * @param techniqueId The ID of the technique to which this subtechnique belongs.
 */
public record Subtechnique(String id, String name, String description, String techniqueId) {
}
