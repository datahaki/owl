// code by jph
package ch.alpine.owl.bot.psu;

import java.util.Collection;

import ch.alpine.owl.data.tree.NodesAssert;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.core.CheckedTrajectoryPlanner;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.HeuristicAssert;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import junit.framework.TestCase;

public class PsuDemoTest extends TestCase {
  public void testFindGoal() {
    GoalInterface goalInterface = PsuGoalManager.of( //
        PsuMetric.INSTANCE, Tensors.vector(Math.PI * 0.7, 0.5), RealScalar.of(0.3));
    TrajectoryPlanner trajectoryPlanner = CheckedTrajectoryPlanner.wrap(PsuDemo.raw(goalInterface));
    HeuristicAssert.check(trajectoryPlanner);
    assertFalse(trajectoryPlanner.getBest().isPresent());
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    assertFalse(trajectoryPlanner.getBest().isPresent());
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    HeuristicAssert.check(trajectoryPlanner);
    assertTrue(trajectoryPlanner.getBest().isPresent());
    Collection<GlcNode> collection = trajectoryPlanner.getDomainMap().values();
    assertTrue(100 < collection.size());
    NodesAssert.containsOneRoot(collection);
  }
}
