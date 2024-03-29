// code by jph
package ch.alpine.owl.bot.tse2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class Tse2VelocityConstraintTest {
  @Test
  void testSimple() {
    PlannerConstraint plannerConstraint = //
        new Tse2VelocityConstraint(Quantity.of(-3, "m*s^-1"), Quantity.of(5, "m*s^-1"));
    assertTrue(plannerConstraint.isSatisfied(null, //
        Arrays.asList(new StateTime(Tensors.fromString("{-Infinity, -Infinity, -Infinity, 2[m*s^-1]}"), RealScalar.of(2))), null));
    assertFalse(plannerConstraint.isSatisfied(null, //
        Arrays.asList(new StateTime(Tensors.fromString("{-Infinity, -Infinity, -Infinity, 6[m*s^-1]}"), RealScalar.of(2))), null));
  }

  @Test
  void testEquals() {
    PlannerConstraint plannerConstraint = //
        new Tse2VelocityConstraint(Quantity.of(5, "m*s^-1"), Quantity.of(5, "m*s^-1"));
    assertTrue(plannerConstraint.isSatisfied(null, //
        Arrays.asList(new StateTime(Tensors.fromString("{-Infinity, -Infinity, -Infinity, 5[m*s^-1]}"), RealScalar.of(2))), null));
    assertFalse(plannerConstraint.isSatisfied(null, //
        Arrays.asList(new StateTime(Tensors.fromString("{-Infinity, -Infinity, -Infinity, 6[m*s^-1]}"), RealScalar.of(2))), null));
  }
}
