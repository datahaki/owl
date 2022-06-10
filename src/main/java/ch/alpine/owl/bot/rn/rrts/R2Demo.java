// code by jph
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.adapter.RrtsNodes;
import ch.alpine.owl.rrts.core.DefaultRrts;
import ch.alpine.owl.rrts.core.Rrts;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.util.win.OwlFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.sophus.crv.TransitionSpace;
import ch.alpine.sophus.lie.rn.RnTransitionSpace;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

/* package */ enum R2Demo {
  ;
  static OwlFrame show() {
    int wid = 7;
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(wid, wid));
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(coordinateBoundingBox);
    TransitionRegionQuery transitionRegionQuery = StaticHelper.polygon1();
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).orElseThrow();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(coordinateBoundingBox);
    for (int count = 0; count < 1000; ++count)
      rrts.insertAsNode(RandomSample.of(randomSampleInterface), 15);
    System.out.println("rewireCount=" + rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.geometricComponent.setOffset(42, 456);
    owlFrame.jFrame.setBounds(100, 100, 500, 500);
    owlFrame.setRrts(transitionSpace, root, transitionRegionQuery);
    return owlFrame;
  }

  public static void main(String[] args) {
    show();
  }
}
