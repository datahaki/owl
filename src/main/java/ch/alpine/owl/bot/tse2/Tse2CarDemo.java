// code by jph
package ch.alpine.owl.bot.tse2;

import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.api.Region;
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
  public final OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    configure(owlAnimationFrame);
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame;
  }

  abstract void configure(OwlAnimationFrame owlAnimationFrame);
}
