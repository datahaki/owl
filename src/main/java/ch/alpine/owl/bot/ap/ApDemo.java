// code by astoll
package ch.alpine.owl.bot.ap;

import java.util.List;
import java.util.Optional;

import ch.alpine.java.win.OwlFrame;
import ch.alpine.java.win.OwlGui;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Degree;

/** simple animation of a landing airplane */
/* package */ enum ApDemo {
  ;
  final static Tensor INITIAL = Tensors.of(RealScalar.of(0), RealScalar.of(80), RealScalar.of(60), Degree.of(-1));

  public static void main(String[] args) {
    // StateTimeRaster stateTimeRaster = ApTrajectoryPlanner.stateTimeRaster();
    StandardTrajectoryPlanner standardTrajectoryPlanner = ApTrajectoryPlanner.apStandardTrajectoryPlanner();
    // ---
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.geometricComponent.setOffset(300, 300);
    owlFrame.geometricComponent.setModel2Pixel(Tensors.fromString("{{1, 0, 10}, {0, -1, 500}, {0, 0, 1}}"));
    // owlyFrame.addBackground(RegionRenders.create(region));
    // owlyFrame.addBackground(RegionRenders.create(sphericalRegion));
    // owlyFrame.addBackground(RenderElements.create(stateTimeRaster));
    // owlyFrame.addBackground(RenderElements.create(plannerConstraint));
    // owlyFrame.addBackground(new DomainRender(trajectoryPlanner.getDomainMap(), eta));
    // ---
    System.out.println("Initial starting point: " + INITIAL);
    System.out.println("Final desired point: " + ApTrajectoryPlanner.GOAL);
    // ---
    standardTrajectoryPlanner.insertRoot(new StateTime(INITIAL, RealScalar.ZERO));
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
    owlFrame.setGlc(standardTrajectoryPlanner);
    // OwlyFrame owlyFrame2 = OwlyGui.glc(standardTrajectoryPlanner);
  }
}
