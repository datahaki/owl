// code by jph
package ch.alpine.sophus.sym;

import java.io.IOException;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class SymScalarTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Serialization.copy(SymScalar.leaf(3)).hashCode();
    SymScalar.of(SymScalar.leaf(2), SymScalar.leaf(3), RationalScalar.HALF).hashCode();
  }

  public void testFail() {
    AssertFail.of(() -> SymScalar.of(SymScalar.leaf(2), RealScalar.of(3), RationalScalar.HALF));
  }
}
