// code by jph
package ch.alpine.owl.bot.se2.rl;

import ch.alpine.java.win.DemoInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.api.Region;
import ch.alpine.subare.core.td.SarsaType;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Se2PolicyImageDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._2181;
    Region<Tensor> region = r2ImageRegionWrap.region();
    // ---
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeInvariant(region);
    owlAnimationFrame.addBackground(RegionRenders.create(region));
    owlAnimationFrame.addBackground(RegionRenders.create(trq));
    // ---
    {
      CarPolicyEntity carPolicyEntity = //
          new CarPolicyEntity(Tensors.vector(2.383, 2.567, 1.571), SarsaType.QLEARNING, trq);
      owlAnimationFrame.add(carPolicyEntity);
    }
    {
      CarPolicyEntity twdPolicyEntity = //
          new CarPolicyEntity(Tensors.vector(1.3, 2.8, 1.57), SarsaType.EXPECTED, trq);
      owlAnimationFrame.add(twdPolicyEntity);
    }
    // ---
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new Se2PolicyImageDemo().start().jFrame.setVisible(true);
  }
}
