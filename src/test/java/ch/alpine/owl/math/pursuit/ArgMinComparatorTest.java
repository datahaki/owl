// code by jph
package ch.alpine.owl.math.pursuit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;

public class ArgMinComparatorTest {
  @Test
  public void testSimple() {
    int r1 = ArgMinComparator.INSTANCE.compare(Tensors.vector(1), Tensors.vector(2));
    int r2 = Integer.compare(1, 2);
    int r3 = Scalars.compare(RealScalar.of(1), RealScalar.of(2));
    assertEquals(r1, r2);
    assertEquals(r1, r3);
  }

  @Test
  public void testNull1st() {
    int r1 = ArgMinComparator.INSTANCE.compare(null, Tensors.vector(2));
    int r2 = Integer.compare(2, 1);
    int r3 = Scalars.compare(RealScalar.of(2), RealScalar.of(1));
    assertEquals(r1, r2);
    assertEquals(r1, r3);
  }

  @Test
  public void testNull2nd() {
    int r1 = ArgMinComparator.INSTANCE.compare(Tensors.vector(2), null);
    int r2 = Integer.compare(1, 2);
    int r3 = Scalars.compare(RealScalar.of(1), RealScalar.of(2));
    assertEquals(r1, r2);
    assertEquals(r1, r3);
  }
}
