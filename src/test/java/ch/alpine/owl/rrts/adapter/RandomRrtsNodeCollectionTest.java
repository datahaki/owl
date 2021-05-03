// code by jph
package ch.alpine.owl.rrts.adapter;

import java.util.Collection;

import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class RandomRrtsNodeCollectionTest extends TestCase {
  public void testSimple() {
    RandomRrtsNodeCollection randomRrtsNodeCollection = new RandomRrtsNodeCollection();
    Collection<RrtsNode> collection = randomRrtsNodeCollection.nearFrom(Tensors.empty(), 10);
    assertTrue(collection.isEmpty());
  }
}
