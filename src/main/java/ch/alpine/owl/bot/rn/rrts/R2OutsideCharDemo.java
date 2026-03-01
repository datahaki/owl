// code by jph
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.rrts.adapter.LengthCostFunction;
import ch.alpine.owlets.rrts.adapter.RrtsNodes;
import ch.alpine.owlets.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owlets.rrts.core.DefaultRrts;
import ch.alpine.owlets.rrts.core.Rrts;
import ch.alpine.owlets.rrts.core.RrtsNode;
import ch.alpine.owlets.rrts.core.RrtsNodeCollection;
import ch.alpine.owlets.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.ts.RnTransitionSpace;
import ch.alpine.sophis.ts.TransitionSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

/* package */ enum R2OutsideCharDemo {
  ;
  static void main() throws Exception {
    CoordinateBoundingBox box = CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(7, 7));
    MemberQ imageRegion = R2ImageRegions.outside_0b36(box.max());
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(box);
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery(imageRegion, RealScalar.of(0.1));
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).orElseThrow();
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(box);
    int frame = 0;
    while (frame++ < 20) {
      for (int count = 0; count < 50; ++count)
        rrts.insertAsNode(RandomSample.of(randomSampleInterface), 15);
      Thread.sleep(10);
    }
    TimerFrame owlFrame = OwlGui.rrts(transitionSpace, root, transitionRegionQuery);
    owlFrame.geometricComponent.setOffset(60, 477);
    owlFrame.jFrame.setBounds(100, 100, 550, 550);
    owlFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(imageRegion));
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
  }
}
