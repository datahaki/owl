// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.List;
import java.util.Optional;

import ch.alpine.owlets.data.tree.Expand;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.math.state.StateTime;

/* package */ enum R2SphereDemo {
  ;
  static void main() {
    R2SphereBase r2SphereBase = new R2SphereBase();
    TrajectoryPlanner trajectoryPlanner = r2SphereBase.create();
    Expand<GlcNode> expand = new Expand<>(trajectoryPlanner);
    expand.steps(200);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    r2SphereBase.show(trajectoryPlanner);
  }
}
