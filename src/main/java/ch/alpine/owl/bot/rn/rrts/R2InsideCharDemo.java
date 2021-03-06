// code by jph
package ch.alpine.owl.bot.rn.rrts;

import java.awt.image.BufferedImage;

import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.gui.win.OwlyFrame;
import ch.alpine.owl.gui.win.OwlyGui;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.adapter.RrtsNodes;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.core.DefaultRrts;
import ch.alpine.owl.rrts.core.Rrts;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ enum R2InsideCharDemo {
  ;
  private static void explore(BufferedImage bufferedImage, Tensor range, Tensor start) throws Exception {
    Region<Tensor> region = ImageRegion.of(bufferedImage, range, false);
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(Tensors.vector(0, 0), range);
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery(region, RealScalar.of(0.1));
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(start, 5).get();
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.geometricComponent.setOffset(60, 477);
    owlyFrame.jFrame.setBounds(100, 100, 650, 550);
    owlyFrame.addBackground(RegionRenders.create(region));
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(Tensors.vector(0, 0), range);
    int frame = 0;
    while (frame++ < 20 && owlyFrame.jFrame.isVisible()) {
      for (int count = 0; count < 50; ++count)
        rrts.insertAsNode(RandomSample.of(randomSampleInterface), 15);
      owlyFrame.setRrts(transitionSpace, root, transitionRegionQuery);
      Thread.sleep(10);
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
  }

  public static void _0b36() throws Exception {
    explore(R2ImageRegions.inside_0b36(), Tensors.vector(6, 7), Tensors.vector(1.8, 2.7));
  }

  public static void _265b() throws Exception {
    explore(R2ImageRegions.inside_265b(), Tensors.vector(7, 7), Tensors.vector(1.833, 2.5));
  }

  public static void _2182() throws Exception {
    explore(R2ImageRegions.inside_2182(), Tensors.vector(9, 6), Tensors.vector(4.5, 3));
  }

  public static void main(String[] args) throws Exception {
    _0b36();
  }
}
