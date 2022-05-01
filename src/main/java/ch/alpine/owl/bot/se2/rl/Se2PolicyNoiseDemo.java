// code by jph
package ch.alpine.owl.bot.se2.rl;

import ch.alpine.java.win.DemoInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.api.Region;
import ch.alpine.subare.core.td.SarsaType;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Se2PolicyNoiseDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    // ---
    final Scalar threshold = RealScalar.of(0.6);
    Region<Tensor> region = new R2NoiseRegion(threshold);
    // ---
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeInvariant(region);
    owlAnimationFrame.addBackground(RegionRenders.create(trq));
    // ---
    Tensor start = Tensors.vector(2.000, 3.317, 0.942).unmodifiable();
    owlAnimationFrame.add(new CarPolicyEntity(start, SarsaType.QLEARNING, trq));
    owlAnimationFrame.add(new CarPolicyEntity(start, SarsaType.EXPECTED, trq));
    owlAnimationFrame.add(new CarPolicyEntity(start, SarsaType.ORIGINAL, trq));
    // ---
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new Se2PolicyNoiseDemo().start().jFrame.setVisible(true);
  }
}
