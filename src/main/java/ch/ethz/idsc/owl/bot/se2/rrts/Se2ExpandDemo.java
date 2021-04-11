// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.owl.gui.win.OwlyFrame;
import ch.ethz.idsc.owl.gui.win.OwlyGui;
import ch.ethz.idsc.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.owl.rrts.adapter.RrtsNodes;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparators;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;

/* package */ enum Se2ExpandDemo {
  ;
  private static void animate(TransitionSpace transitionSpace) throws IOException, Exception {
    Tensor min = Tensors.vector(0, 0, -Math.PI);
    Tensor max = Tensors.vector(7, 7, +Math.PI);
    RrtsNodeCollection rrtsNodeCollection = //
        Se2RrtsNodeCollections.of(transitionSpace, min.extract(0, 2), max.extract(0, 2));
    TransitionRegionQuery transitionRegionQuery = EmptyTransitionRegionQuery.INSTANCE;
    // ---
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 5).get();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(min, max);
    String name = "se2rrts_" + transitionSpace.getClass().getSimpleName() + ".gif";
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures(name), 250, TimeUnit.MILLISECONDS)) {
      OwlyFrame owlyFrame = OwlyGui.start();
      owlyFrame.geometricComponent.setOffset(42, 456);
      owlyFrame.jFrame.setBounds(100, 100, 500, 500);
      // owlyFrame.geometricComponent.addRenderInterface(renderInterface);
      for (int frame = 0; frame < 40 && owlyFrame.jFrame.isVisible(); ++frame) {
        for (int count = 0; count < 5; ++count)
          rrts.insertAsNode(RandomSample.of(randomSampleInterface), 20);
        owlyFrame.setRrts(transitionSpace, root, transitionRegionQuery);
        animationWriter.write(owlyFrame.offscreen());
      }
      int repeatLast = 3;
      while (0 < repeatLast--)
        animationWriter.write(owlyFrame.offscreen());
      owlyFrame.close();
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
  }

  public static void main(String[] args) throws Exception {
    animate(DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH));
    animate(ClothoidTransitionSpace.ANALYTIC);
  }
}
