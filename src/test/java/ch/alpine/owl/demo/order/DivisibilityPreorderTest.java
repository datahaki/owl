// code by jph
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.order.OrderComparison;
import ch.alpine.tensor.RealScalar;

public class DivisibilityPreorderTest {
  @Test
  public void testSimple() {
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(1), RealScalar.of(6)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(6), RealScalar.of(6)), OrderComparison.INDIFFERENT);
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(6), RealScalar.of(2)), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(DivisibilityPreorder.INSTANCE.compare(RealScalar.of(6), RealScalar.of(7)), OrderComparison.INCOMPARABLE);
  }

  @Test
  public void testZeroFail() {
    AssertFail.of(() -> DivisibilityPreorder.INSTANCE.compare(RealScalar.ZERO, RealScalar.ONE));
    AssertFail.of(() -> DivisibilityPreorder.INSTANCE.compare(RealScalar.ONE, RealScalar.ZERO));
  }
}
