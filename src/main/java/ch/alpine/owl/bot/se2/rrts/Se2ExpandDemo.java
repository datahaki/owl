// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ch.alpine.owl.rrts.adapter.EmptyTransitionRegionQuery;
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
import ch.alpine.sophus.crv.clt.ClothoidTransitionSpace;
import ch.alpine.sophus.crv.dub.DubinsPathComparators;
import ch.alpine.sophus.crv.dub.DubinsTransitionSpace;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.AnimationWriter;
import ch.alpine.tensor.io.GifAnimationWriter;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

/* package */ enum Se2ExpandDemo {
  ;
  private static void animate(TransitionSpace transitionSpace) throws IOException, Exception {
    Tensor min = Tensors.vector(0, 0, -Math.PI);
    Tensor max = Tensors.vector(7, 7, +Math.PI);
    RrtsNodeCollection rrtsNodeCollection = //
        new Se2RrtsNodeCollection(transitionSpace, CoordinateBounds.of(min.extract(0, 2), max.extract(0, 2)), 3);
    TransitionRegionQuery transitionRegionQuery = EmptyTransitionRegionQuery.INSTANCE;
    // ---
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 5).get();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(CoordinateBounds.of(min, max));
    String name = "se2rrts_" + transitionSpace.getClass().getSimpleName() + ".gif";
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures(name), 250, TimeUnit.MILLISECONDS)) {
      OwlFrame owlFrame = OwlGui.start();
      owlFrame.geometricComponent.setOffset(42, 456);
      owlFrame.jFrame.setBounds(100, 100, 500, 500);
      // owlyFrame.geometricComponent.addRenderInterface(renderInterface);
      for (int frame = 0; frame < 40 && owlFrame.jFrame.isVisible(); ++frame) {
        for (int count = 0; count < 5; ++count)
          rrts.insertAsNode(RandomSample.of(randomSampleInterface), 20);
        owlFrame.setRrts(transitionSpace, root, transitionRegionQuery);
        animationWriter.write(owlFrame.offscreen());
      }
      int repeatLast = 3;
      while (0 < repeatLast--)
        animationWriter.write(owlFrame.offscreen());
      owlFrame.close();
    }
    System.out.println(rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
  }

  public static void main(String[] args) throws Exception {
    animate(DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH));
    animate(ClothoidTransitionSpace.ANALYTIC);
  }
}
