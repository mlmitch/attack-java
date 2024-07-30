package com.wassonlabs.attack;

import java.util.List;

public record Tactic(String id, String name, String description, List<String> techniqueIds) {
}
