// code by jph
package ch.alpine.owl.bot.tse2;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;

/* package */ abstract class Tse2CarDemo implements DemoInterface {
  private static final Tensor PROBE_X = Tensors.vector(0.2, 0.1, 0, -0.1);

  static MemberQ line(MemberQ region) {
    return Se2PointsVsRegions.line(PROBE_X, region);
  }

  static PlannerConstraint createConstraint(MemberQ region) {
    return RegionConstraints.timeInvariant(line(region));
  }

  @Override // from DemoInterface
  public final TimerFrame getWindow() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    configure(owlAnimationFrame);
    owlAnimationFrame.timerFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame.timerFrame;
  }

  abstract void configure(OwlAnimationFrame owlAnimationFrame);
}
