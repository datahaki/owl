// code by jph
package ch.alpine.owl.rrts.adapter;

import java.util.Collection;

import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class ExhaustiveRrtsNodeCollectionTest extends TestCase {
  public void testSimple() {
    RrtsNodeCollection rrtsNodeCollection = //
        ExhaustiveRrtsNodeCollection.of(RnTransitionSpace.INSTANCE);
    RrtsNode rn3 = RrtsNode.createRoot(Tensors.vector(9, 0), RealScalar.ONE);
    rrtsNodeCollection.insert(rn3);
    RrtsNode rn1 = RrtsNode.createRoot(Tensors.vector(0, 0), RealScalar.ONE);
    rrtsNodeCollection.insert(rn1);
    RrtsNode rn2 = RrtsNode.createRoot(Tensors.vector(5, 0), RealScalar.ONE);
    rrtsNodeCollection.insert(rn2);
    {
      Collection<RrtsNode> collection = rrtsNodeCollection.nearFrom(Tensors.vector(2, 1), 2);
      assertTrue(collection.contains(rn1));
      assertTrue(collection.contains(rn2));
      assertFalse(collection.contains(rn3));
    }
    {
      Collection<RrtsNode> collection = rrtsNodeCollection.nearFrom(Tensors.vector(8, 1), 1);
      assertFalse(collection.contains(rn1));
      assertFalse(collection.contains(rn2));
      assertTrue(collection.contains(rn3));
    }
  }
}
