// code by jph
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.java.win.OwlFrame;
import ch.alpine.java.win.OwlGui;
import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.adapter.RrtsNodes;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.core.DefaultRrts;
import ch.alpine.owl.rrts.core.Rrts;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

/* package */ enum R2ImageDemo {
  ;
  public static void main(String[] args) throws Exception {
    Tensor range = Tensors.vector(7, 7);
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(Array.zeros(2), range);
    Region<Tensor> imageRegion = //
        ImageRegions.loadFromRepository("/io/track0_100.png", range, false);
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(coordinateBoundingBox);
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery( //
        imageRegion, RealScalar.of(0.1));
    // ---
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).orElseThrow();
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.geometricComponent.setOffset(60, 477);
    owlFrame.jFrame.setBounds(100, 100, 550, 550);
    owlFrame.addBackground(RegionRenders.create(imageRegion));
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(coordinateBoundingBox);
    int frame = 0;
    while (frame++ < 20 && owlFrame.jFrame.isVisible()) {
      for (int c = 0; c < 50; ++c)
        rrts.insertAsNode(RandomSample.of(randomSampleInterface), 15);
      owlFrame.setRrts(transitionSpace, root, transitionRegionQuery);
      Thread.sleep(10);
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
  }
}
