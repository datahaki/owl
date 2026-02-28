// code by jph
package ch.alpine.owl.bot.rn.rrts;

import java.awt.Container;

import ch.alpine.ascony.win.GeometricComponent;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.owl.util.ren.RenderElements;
import ch.alpine.owlets.rrts.adapter.LengthCostFunction;
import ch.alpine.owlets.rrts.adapter.RrtsNodes;
import ch.alpine.owlets.rrts.core.DefaultRrts;
import ch.alpine.owlets.rrts.core.Rrts;
import ch.alpine.owlets.rrts.core.RrtsNode;
import ch.alpine.owlets.rrts.core.RrtsNodeCollection;
import ch.alpine.owlets.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.ts.RnTransitionSpace;
import ch.alpine.sophis.ts.TransitionSpace;
import ch.alpine.sophus.rsm.BallRandomSample;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

@ReflectionMarker
class R2NoiseDemo implements ManipulateProvider {
  public Integer numel = 1000;
  // ---
  private final GeometricComponent geometricComponent = new GeometricComponent();

  @Override
  public Container getContainer() {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(Tensors.vector(-1, -3), Tensors.vector(-1 + 6, -3 + 6));
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(coordinateBoundingBox);
    TransitionRegionQuery transitionRegionQuery = StaticHelper.noise1();
    TransitionSpace transitionSpace = RnTransitionSpace.INSTANCE;
    Rrts rrts = new DefaultRrts(transitionSpace, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 5).orElseThrow();
    RandomSampleInterface randomSampleInterface = //
        BallRandomSample.of(Tensors.vector(2, 0), RealScalar.of(3));
    for (int c = 0; c < numel; ++c)
      rrts.insertAsNode(RandomSample.of(randomSampleInterface), 15);
    System.out.println("rewireCount=" + rrts.rewireCount());
    RrtsNodes.costConsistency(root, transitionSpace, LengthCostFunction.INSTANCE);
    RenderElements.setRrts(geometricComponent, transitionSpace, root, transitionRegionQuery);
    return geometricComponent.jComponent;
  }

  static void main() {
    new R2NoiseDemo().runStandalone();
  }
}
