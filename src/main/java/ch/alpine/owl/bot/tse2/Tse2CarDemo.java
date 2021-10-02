// code by jph
package ch.alpine.owl.bot.tse2;

import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ abstract class Tse2CarDemo implements DemoInterface {
  private static final Tensor PROBE_X = Tensors.vector(0.2, 0.1, 0, -0.1);

  static Region<Tensor> line(Region<Tensor> region) {
    return Se2PointsVsRegions.line(PROBE_X, region);
  }

  static PlannerConstraint createConstraint(Region<Tensor> region) {
    return RegionConstraints.timeInvariant(line(region));
  }

  @Override // from DemoInterface
  public final OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    configure(owlyAnimationFrame);
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  abstract void configure(OwlyAnimationFrame owlyAnimationFrame);
}
