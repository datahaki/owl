// code by jph
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.gui.win.OwlyFrame;
import ch.alpine.owl.gui.win.OwlyGui;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.adapter.RrtsNodes;
import ch.alpine.owl.rrts.core.DefaultRrts;
import ch.alpine.owl.rrts.core.Rrts;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ enum R2Demo {
  ;
  static OwlyFrame show() {
    int wid = 7;
    Tensor min = Tensors.vector(0, 0);
    Tensor max = Tensors.vector(wid, wid);
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(min, max);
    TransitionRegionQuery transitionRegionQuery = StaticHelper.polygon1();
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).get();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(min, max);
    for (int count = 0; count < 1000; ++count)
      rrts.insertAsNode(RandomSample.of(randomSampleInterface), 15);
    System.out.println("rewireCount=" + rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.geometricComponent.setOffset(42, 456);
    owlyFrame.jFrame.setBounds(100, 100, 500, 500);
    owlyFrame.setRrts(transitionSpace, root, transitionRegionQuery);
    return owlyFrame;
  }

  public static void main(String[] args) {
    show();
  }
}
