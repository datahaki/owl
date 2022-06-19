// code by jph
package ch.alpine.owl.glc.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class DomainQueueTest {
  @Test
  void testQueue() {
    GlcNode first = GlcNode.of(null, null, RealScalar.of(1), RealScalar.ZERO);
    DomainQueue dq = DomainQueue.singleton(first);
    dq.add(GlcNode.of(null, null, RealScalar.of(0), RealScalar.ZERO));
    dq.add(GlcNode.of(null, null, RealScalar.of(9), RealScalar.ZERO));
    GlcNode n1 = dq.poll();
    assertEquals(n1.costFromRoot().number(), 0);
    GlcNode n2 = dq.poll();
    assertEquals(n2.costFromRoot().number(), 1);
    GlcNode n3 = dq.poll();
    assertEquals(n3.costFromRoot().number(), 9);
    assertTrue(dq.isEmpty());
  }

  /** the test shows that removing elements
   * from the collection Map::values
   * also removes the entry in the map */
  @Test
  void testMap() {
    Map<Integer, String> map = new HashMap<>();
    map.put(1, "one");
    map.put(2, "two");
    map.put(3, "three");
    Collection<String> asd = map.values();
    asd.remove("two");
    assertEquals(map.size(), 2);
    assertFalse(map.containsKey(2));
  }
}
