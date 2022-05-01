// code by astoll
package ch.alpine.owl.bot.balloon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class BalloonPlannerConstraintTest {
  private static final PlannerConstraint PLANNER_CONSTRAINT = //
      new BalloonPlannerConstraint(Quantity.of(5, "m*s^-1"));
  private static final Scalar TIME = Quantity.of(2, "s");
  private static final Tensor FLOW = Tensors.vector(100, 0.1);

  @Test
  public void testYConstraints() {
    Tensor yUnvalid = Tensors.fromString("{5[m], -3[m], 4[m*s^-1], 40[m * K^-1 * s^-2]}");
    GlcNode pseudoNodeZ = GlcNode.of(FLOW, new StateTime(yUnvalid, TIME), RealScalar.ONE, RealScalar.ONE);
    assertFalse(PLANNER_CONSTRAINT.isSatisfied(pseudoNodeZ, null, FLOW));
  }

  @Test
  public void testVConstraints() {
    Tensor vUnvalidMax = Tensors.fromString("{5[m], 0[m], 6[m*s^-1], 40[m * K^-1 * s^-2]}");
    Tensor vUnvalidNegative = Tensors.fromString("{5[m], 0[m], -4[m*s^-1], 40[m * K^-1 * s^-2]}");
    GlcNode pseudoNodeVNegative = GlcNode.of(FLOW, new StateTime(vUnvalidNegative, TIME), RealScalar.ONE, RealScalar.ONE);
    GlcNode pseudoNodeVMax = GlcNode.of(FLOW, new StateTime(vUnvalidMax, TIME), RealScalar.ONE, RealScalar.ONE);
    assertTrue(PLANNER_CONSTRAINT.isSatisfied(pseudoNodeVNegative, null, FLOW));
    assertFalse(PLANNER_CONSTRAINT.isSatisfied(pseudoNodeVMax, null, FLOW));
  }

  @Test
  public void testValidConstraints() {
    Tensor valid = Tensors.fromString("{5[m], 0[m], 4[m*s^-1], 40[m * K^-1 * s^-2]}");
    GlcNode pseudoNodeValid = GlcNode.of(FLOW, new StateTime(valid, TIME), RealScalar.ONE, RealScalar.ONE);
    assertTrue(PLANNER_CONSTRAINT.isSatisfied(pseudoNodeValid, null, FLOW));
  }
}
