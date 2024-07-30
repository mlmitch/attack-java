package com.wassonlabs.attack;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnterpriseAttackTest {

    @Test
    public void test() {
        String tacticId = "TA0001";
        Tactic t = EnterpriseAttack.getTactic(tacticId);
        Assertions.assertEquals(tacticId, t.id());

        String techniqueId = "T1583";
        Technique tech = EnterpriseAttack.getTechnique(techniqueId);
        Assertions.assertEquals(techniqueId, tech.id());

        String subtechniqueId = "T1583.002";
        Subtechnique subtech = EnterpriseAttack.getSubtechnique(subtechniqueId);
        Assertions.assertEquals(subtechniqueId, subtech.id());
        Assertions.assertEquals(techniqueId, subtech.techniqueId());
    }

}
