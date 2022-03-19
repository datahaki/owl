// code by jph
package ch.alpine.owl.bot.se2.glc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.rl2.RelaxedGlcExpand;
import ch.alpine.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Se2RelaxedCornerCuttingDemoTest {
  @Test
  public void testSimple() {
    StateTime stateTime = new StateTime(Tensors.vector(1.7, 2.2, 0), RealScalar.ZERO);
    Tensor slacks = Tensors.vector(2, 0);
    CarRelaxedEntity carRelaxedEntity = CarRelaxedEntity.createDefault(stateTime, slacks);
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = Se2RelaxedCornerCuttingDemo.createResLo();
    carRelaxedEntity.setAdditionalCostFunction(r2ImageRegionWrap.costFunction());
    Region<Tensor> region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = Se2CarDemo.createConstraint(region);
    Tensor goal = Tensors.vector(4.183, 5.017, 1.571);
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = //
        carRelaxedEntity.createTreePlanner(plannerConstraint, goal);
    relaxedTrajectoryPlanner.insertRoot(stateTime);
    RelaxedGlcExpand relaxedGlcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    relaxedGlcExpand.findAny(1000);
    int count = relaxedGlcExpand.getExpandCount();
    assertTrue(count < 800);
    relaxedGlcExpand.untilOptimal(300);
  }
}
