// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.util.Collection;

import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.crv.dubins.DubinsPathComparators;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Se2RrtsNodeCollectionsTest extends TestCase {
  private static void _check(TransitionSpace transitionSpace) {
    Tensor lbounds = Tensors.fromString("{-5[m], -7[m]}");
    Tensor ubounds = Tensors.fromString("{10[m], 10[m]}");
    RrtsNodeCollection rrtsNodeCollection = //
        Se2RrtsNodeCollections.of(transitionSpace, lbounds, ubounds);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        Append.of(lbounds, Pi.VALUE.negate()), //
        Append.of(ubounds, Pi.VALUE));
    for (int count = 0; count < 30; ++count) {
      Tensor tensor = RandomSample.of(randomSampleInterface);
      rrtsNodeCollection.insert(RrtsNode.createRoot(tensor, RealScalar.ONE));
    }
    Collection<RrtsNode> collection = rrtsNodeCollection.nearTo(Tensors.fromString("{2[m], 3[m], 1.2}"), 10);
    assertEquals(collection.size(), 10);
  }

  public void testClothoid() {
    _check(ClothoidTransitionSpace.ANALYTIC);
  }

  public void testDubins() {
    _check(DubinsTransitionSpace.of(Quantity.of(2, "m"), DubinsPathComparators.LENGTH));
  }
}
