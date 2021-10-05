// code by jph
package ch.alpine.sophus.demo.opt;

import ch.alpine.sophus.crv.d2.PolygonArea;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class SpearheadTest extends TestCase {
  public void testSimple() {
    Tensor polygon = Spearhead.of(Tensors.vector(-0.806, -0.250, -0.524), RealScalar.of(0.1));
    Sign.requirePositive(PolygonArea.of(polygon));
  }
}
