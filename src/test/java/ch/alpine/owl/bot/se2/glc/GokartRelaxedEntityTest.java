// code by ynager, jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.glc.adapter.ConstraintViolationCost;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.rl2.RelaxedGlcExpand;
import ch.alpine.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class GokartRelaxedEntityTest extends TestCase {
  public void testSimple() {
    final StateTime initial = new StateTime(Tensors.vector(0, 10, 0), RealScalar.ZERO);
    // define region costs
    // define slack vector
    Tensor slacks = Tensors.vector(0, 0);
    GokartRelaxedEntity gokartRelaxedEntity = GokartRelaxedEntity.createRelaxedGokartEntity(initial, slacks);
    gokartRelaxedEntity.setAdditionalCostFunction(ConstraintViolationCost.of(EmptyPlannerConstraint.INSTANCE, RealScalar.ONE));
    Tensor goal = Tensors.vector(0, 25, 0);
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = gokartRelaxedEntity.createTreePlanner(EmptyPlannerConstraint.INSTANCE, goal);
    assertEquals(gokartRelaxedEntity.getSlack(), slacks);
    relaxedTrajectoryPlanner.insertRoot(initial);
    RelaxedGlcExpand glcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    glcExpand.findAny(1000);
    assertTrue(relaxedTrajectoryPlanner.getBest().isPresent());
  }
}
