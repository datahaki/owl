// code by jph
package ch.alpine.sophus.sym;

import java.util.stream.IntStream;

import ch.alpine.sophus.crv.bezier.BezierFunction;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;
import junit.framework.TestCase;

public class SymLinkBuilderTest extends TestCase {
  public void testSimple() {
    Tensor control = Tensors.vector(1, 2, 3);
    Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf));
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
    SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(RealScalar.of(0.3));
    // ---
    SymLink symLink = SymLinkBuilder.of(control, symScalar);
    assertNotNull(symLink);
  }
}
