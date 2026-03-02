// code by astoll
package ch.alpine.owl.bot.ap;

import java.util.List;
import java.util.Optional;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

/** simple animation of a landing airplane */
/* package */ enum ApDemo {
  ;
  final static Tensor INITIAL = Tensors.fromString("{0[m], 80[m], 60[m*s^-1], -0.017}");

  static void main() {
    // StateTimeRaster stateTimeRaster = ApTrajectoryPlanner.stateTimeRaster();
    StandardTrajectoryPlanner standardTrajectoryPlanner = ApTrajectoryPlanner.apStandardTrajectoryPlanner();
    // ---
    // owlyFrame.addBackground(RegionRenders.create(region));
    // owlyFrame.addBackground(RegionRenders.create(sphericalRegion));
    // owlyFrame.addBackground(RenderElements.create(stateTimeRaster));
    // owlyFrame.addBackground(RenderElements.create(plannerConstraint));
    // owlyFrame.addBackground(new DomainRender(trajectoryPlanner.getDomainMap(), eta));
    // ---
    System.out.println("Initial starting point: " + INITIAL);
    System.out.println("Final desired point: " + ApTrajectoryPlanner.GOAL);
    // ---
    standardTrajectoryPlanner.insertRoot(new StateTime(INITIAL, Quantity.of(0, "s")));
    GlcExpand glcExpand = new GlcExpand(standardTrajectoryPlanner);
    glcExpand.findAny(15000);
    Optional<GlcNode> optional = standardTrajectoryPlanner.getBest();
    // ---
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    // ---
    if (optional.isPresent()) {
      System.out.println(1);
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    TimerFrame timerFrame = OwlGui.glc(standardTrajectoryPlanner);
    timerFrame.geometricComponent.setOffset(300, 300);
    timerFrame.geometricComponent.setPerPixel(Quantity.of(1, "m^-1"));
    // OwlyFrame owlyFrame2 = OwlyGui.glc(standardTrajectoryPlanner);
  }
}
