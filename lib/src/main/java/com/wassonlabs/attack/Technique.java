package com.wassonlabs.attack;

import java.util.List;

public record Technique(String id, String name, String description, List<String> tacticIds, List<String> subtechniqueIds) {
}
