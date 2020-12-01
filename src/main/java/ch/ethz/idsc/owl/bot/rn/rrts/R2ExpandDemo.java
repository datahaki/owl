// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;

/* package */ enum R2ExpandDemo {
  ;
  public static void main(String[] args) throws Exception {
    int wid = 7;
    Tensor min = Tensors.vector(0, 0);
    Tensor max = Tensors.vector(wid, wid);
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(min, max);
    TransitionRegionQuery transitionRegionQuery = StaticHelper.polygon1();
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).get();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(min, max);
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("r2rrts.gif"), 250, TimeUnit.MILLISECONDS)) {
      OwlyFrame owlyFrame = OwlyGui.start();
      owlyFrame.geometricComponent.setOffset(42, 456);
      owlyFrame.jFrame.setBounds(100, 100, 500, 500);
      int frame = 0;
      while (frame++ < 40 && owlyFrame.jFrame.isVisible()) {
        for (int count = 0; count < 10; ++count)
          rrts.insertAsNode(RandomSample.of(randomSampleInterface), 20);
        owlyFrame.setRrts(transitionSpace, root, transitionRegionQuery);
        animationWriter.write(owlyFrame.offscreen());
      }
      int repeatLast = 3;
      while (0 < repeatLast--)
        animationWriter.write(owlyFrame.offscreen());
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
  }
}
