// code by jph, gjoel
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.gui.win.OwlyFrame;
import ch.alpine.owl.gui.win.OwlyGui;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.adapter.RrtsNodes;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.adapter.TransitionRegionQueryUnion;
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
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Clips;

/* package */ enum Se2ImageDemo {
  ;
  public static void main(String[] args) throws Exception {
    Tensor range = Tensors.vector(7, 7).unmodifiable();
    Region<Tensor> imageRegion = //
        ImageRegions.loadFromRepository("/io/track0_100.png", range, false);
    Tensor lbounds = Array.zeros(2).unmodifiable();
    Tensor ubounds = range.unmodifiable();
    TransitionSpace transitionSpace = ClothoidTransitionSpace.ANALYTIC;
    RrtsNodeCollection rrtsNodeCollection = Se2RrtsNodeCollections.of(transitionSpace, lbounds, ubounds);
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery( //
        imageRegion, RealScalar.of(0.05));
    TransitionRegionQuery transitionCurvatureQuery = new ClothoidCurvatureQuery(Clips.absolute(5));
    TransitionRegionQuery unionTransitionRegionQuery = TransitionRegionQueryUnion.wrap(transitionRegionQuery, transitionCurvatureQuery);
    // ---
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, unionTransitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 5).get();
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.geometricComponent.setOffset(60, 477);
    owlyFrame.jFrame.setBounds(100, 100, 550, 550);
    owlyFrame.addBackground(RegionRenders.create(imageRegion));
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        Append.of(lbounds, Pi.VALUE.negate()), //
        Append.of(ubounds, Pi.VALUE));
    int frame = 0;
    while (frame++ < 20 && owlyFrame.jFrame.isVisible()) {
      for (int c = 0; c < 50; ++c)
        rrts.insertAsNode(RandomSample.of(randomSampleInterface), 15);
      owlyFrame.setRrts(transitionSpace, root, transitionRegionQuery);
      Thread.sleep(10);
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
  }
}
