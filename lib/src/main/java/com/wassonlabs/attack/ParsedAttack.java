package com.wassonlabs.attack;

import java.util.List;

record ParsedAttack(List<Tactic> tactics,
                    List<Technique> techniques,
                    List<Subtechnique> subtechniques) {
}
