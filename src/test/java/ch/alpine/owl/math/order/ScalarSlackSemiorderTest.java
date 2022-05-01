// code by jph
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;

class ScalarSlackSemiorderTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    OrderComparator<Scalar> scalarSlackSemiorder = Serialization.copy(new ScalarSlackSemiorder(Quantity.of(2, "s")));
    assertEquals(scalarSlackSemiorder.compare(Quantity.of(2, "s"), Quantity.of(5, "s")), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(scalarSlackSemiorder.compare(Quantity.of(2, "s"), Quantity.of(1, "s")), OrderComparison.INDIFFERENT);
    assertEquals(scalarSlackSemiorder.compare(Quantity.of(2, "s"), Quantity.of(-2, "s")), OrderComparison.STRICTLY_SUCCEEDS);
  }

  @Test
  public void testZero() {
    ScalarSlackSemiorder scalarSlackSemiorder = new ScalarSlackSemiorder(Quantity.of(0, "kg"));
    assertEquals(scalarSlackSemiorder.compare(Quantity.of(2, "kg"), Quantity.of(2, "kg")), OrderComparison.INDIFFERENT);
    assertEquals(scalarSlackSemiorder.compare(Quantity.of(2.3, "kg"), Quantity.of(2.3, "kg")), OrderComparison.INDIFFERENT);
    // assertEquals(scalarSlackSemiorder.compare(Quantity.of(3, "s"), Quantity.of(2, "s")), OrderComparison.STRICTLY_SUCCEEDS);
    // assertEquals(scalarSlackSemiorder.compare(Quantity.of(-20, "s"), Quantity.of(-2, "s")), OrderComparison.STRICTLY_PRECEDES);
  }

  @Test
  public void testBoundaryCase() {
    ScalarSlackSemiorder scalarSlackSemiorder = new ScalarSlackSemiorder(Quantity.of(1, "m"));
    assertEquals(scalarSlackSemiorder.compare(Quantity.of(2, "m"), Quantity.of(3, "m")), OrderComparison.INDIFFERENT);
    assertEquals(scalarSlackSemiorder.compare(Quantity.of(3, "m"), Quantity.of(2, "m")), OrderComparison.INDIFFERENT);
    assertEquals(scalarSlackSemiorder.compare(Quantity.of(2, "m"), Quantity.of(3.01, "m")), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(scalarSlackSemiorder.compare(Quantity.of(3.01, "m"), Quantity.of(2, "m")), OrderComparison.STRICTLY_SUCCEEDS);
  }
}
