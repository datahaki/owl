// code by jph
package ch.alpine.owl.region;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;

class NegativeHalfspaceRegionTest {
  @Test
  void testSimple() {
    MemberQ r = new NegativeHalfspaceRegion(1);
    assertFalse(r.test(Tensors.vector(1, +1, 1)));
    assertTrue(r.test(Tensors.vector(1, -1, 1)));
  }
}
