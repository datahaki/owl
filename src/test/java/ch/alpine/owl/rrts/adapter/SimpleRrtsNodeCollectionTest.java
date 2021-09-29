// code by jph
package ch.alpine.owl.rrts.adapter;

import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsNodeTransition;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class SimpleRrtsNodeCollectionTest extends TestCase {
  public void testEuclideanNear() {
    Distribution distribution = UniformDistribution.unit();
    RrtsNodeCollection rrtsNodeCollection = new SimpleRrtsNodeCollection(RnTransitionSpace.INSTANCE, LengthCostFunction.INSTANCE);
    for (int index = 0; index < 200; ++index)
      rrtsNodeCollection.insert(RrtsNode.createRoot(RandomVariate.of(distribution, 3), RealScalar.of(10)));
    Tensor center = Tensors.vector(0.5, 0.5, 0.5);
    for (RrtsNodeTransition rrtsNode : rrtsNodeCollection.nearTo(center, 3)) {
      Scalar scalar = Vector2Norm.between(center, rrtsNode.rrtsNode().state());
      assertTrue(Scalars.lessThan(scalar, RealScalar.of(0.3)));
    }
    for (RrtsNodeTransition rrtsNode : rrtsNodeCollection.nearFrom(center, 3)) {
      Scalar scalar = Vector2Norm.between(center, rrtsNode.rrtsNode().state());
      assertTrue(Scalars.lessThan(scalar, RealScalar.of(0.3)));
    }
  }
}
