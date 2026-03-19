// code by jph
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.rrts.adapter.LengthCostFunction;
import ch.alpine.owlets.rrts.adapter.RrtsNodes;
import ch.alpine.owlets.rrts.core.DefaultRrts;
import ch.alpine.owlets.rrts.core.Rrts;
import ch.alpine.owlets.rrts.core.RrtsNode;
import ch.alpine.owlets.rrts.core.RrtsNodeCollection;
import ch.alpine.owlets.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.ts.RnTransitionSpace;
import ch.alpine.sophis.ts.TransitionSpace;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

/* package */ enum R2Demo {
  ;
  static TimerFrame show() {
    int wid = 7;
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(wid, wid));
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(coordinateBoundingBox);
    TransitionRegionQuery transitionRegionQuery = StaticHelper.polygon1();
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).orElseThrow();
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(coordinateBoundingBox);
    for (int count = 0; count < 1000; ++count)
      rrts.insertAsNode(RandomSample.of(randomSampleInterface), 15);
    System.out.println("rewireCount=" + rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
    TimerFrame timerFrame = OwlGui.rrts(transitionSpace, root, transitionRegionQuery);
    timerFrame.setBounds(100, 100, 500, 500);
    return timerFrame;
  }

  static void main() {
    show();
  }
}
