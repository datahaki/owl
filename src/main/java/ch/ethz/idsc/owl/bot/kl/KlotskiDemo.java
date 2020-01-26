// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum KlotskiDemo {
  ;
  static List<StateTime> compute(KlotskiProblem klotskiProblem, boolean print) {
    StateTimeRaster stateTimeRaster = StateTime::state;
    List<Flow> controls = KlotskiControls.of(klotskiProblem.getBoard());
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(HuarongObstacleRegion.INSTANCE);
    // ---
    StandardTrajectoryPlanner standardTrajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, //
        KlotskiIntegrator.INSTANCE, //
        controls, //
        plannerConstraint, //
        new KlotskiGoalAdapter(klotskiProblem.getGoal()));
    standardTrajectoryPlanner.insertRoot(new StateTime(klotskiProblem.getBoard(), RealScalar.ZERO));
    while (true) {
      {
        Optional<GlcNode> optional = standardTrajectoryPlanner.getBest();
        if (optional.isPresent()) {
          GlcNode glcNode = optional.get();
          if (print) {
            System.out.println(glcNode.state());
            System.out.println("BEST IN GOAL");
            System.out.println("$=" + glcNode.costFromRoot());
          }
          return GlcNodes.getPathFromRootTo(glcNode);
        }
      }
      Optional<GlcNode> optional = standardTrajectoryPlanner.pollNext();
      if (optional.isPresent()) {
        Collection<GlcNode> queue = standardTrajectoryPlanner.getQueue();
        Map<Tensor, GlcNode> domainMap = standardTrajectoryPlanner.getDomainMap();
        GlcNode nextNode = optional.get();
        standardTrajectoryPlanner.expand(nextNode);
        for (Entry<Tensor, GlcNode> entry : domainMap.entrySet()) {
          GlcNode glcNode = entry.getValue();
          if (!glcNode.state().equals(entry.getKey())) {
            System.err.println("problem");
          }
        }
        if (print)
          System.out.println(String.format("#=%3d   q=%3d   $=%3s", domainMap.size(), queue.size(), nextNode.costFromRoot()));
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        return null;
      }
    }
  }

  public static void main(String[] args) throws IOException {
    KlotskiProblem klotskiProblem = Pennant.PUZZLE;
    List<StateTime> list = compute(klotskiProblem, true);
    Export.object(HomeDirectory.file(klotskiProblem.name() + ".object"), list);
  }
}
