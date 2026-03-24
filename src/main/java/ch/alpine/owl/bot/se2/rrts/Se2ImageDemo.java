// code by jph, gjoel
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.rrts.adapter.LengthCostFunction;
import ch.alpine.owlets.rrts.adapter.RrtsNodes;
import ch.alpine.owlets.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owlets.rrts.adapter.TransitionRegionQueryUnion;
import ch.alpine.owlets.rrts.core.DefaultRrts;
import ch.alpine.owlets.rrts.core.Rrts;
import ch.alpine.owlets.rrts.core.RrtsNode;
import ch.alpine.owlets.rrts.core.RrtsNodeCollection;
import ch.alpine.owlets.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.ts.ClothoidTransitionSpace;
import ch.alpine.sophis.ts.TransitionSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.sca.Clips;

/* package */ enum Se2ImageDemo {
  ;
  static void main() throws Exception {
    Tensor range = Tensors.vector(7, 7).unmodifiable();
    MemberQ imageRegion = //
        ImageRegions.loadFromRepository("/io/track0_100.png", range, false);
    Tensor lbounds = Array.zeros(2).unmodifiable();
    Tensor ubounds = range.unmodifiable();
    TransitionSpace transitionSpace = ClothoidTransitionSpace.ANALYTIC;
    RrtsNodeCollection rrtsNodeCollection = new Se2RrtsNodeCollection(transitionSpace, CoordinateBounds.of(lbounds, ubounds), 3);
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery( //
        imageRegion, RealScalar.of(0.05));
    TransitionRegionQuery transitionCurvatureQuery = new ClothoidCurvatureQuery(Clips.absolute(5));
    TransitionRegionQuery unionTransitionRegionQuery = TransitionRegionQueryUnion.wrap(transitionRegionQuery, transitionCurvatureQuery);
    // ---
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, unionTransitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 5).get();
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(CoordinateBounds.of( //
        Append.of(lbounds, Pi.VALUE.negate()), //
        Append.of(ubounds, Pi.VALUE)));
    int frame = 0;
    while (frame++ < 20) {
      for (int c = 0; c < 50; ++c)
        rrts.insertAsNode(RandomSample.of(randomSampleInterface), 15);
      Thread.sleep(10);
    }
    TimerFrame timerFrame = OwlGui.rrts(transitionSpace, root, transitionRegionQuery);
    timerFrame.setBounds(100, 100, 550, 550);
    timerFrame.geometricComponent().addRenderInterfaceBackground(RegionRenderFactory.create(imageRegion));
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
  }
}
