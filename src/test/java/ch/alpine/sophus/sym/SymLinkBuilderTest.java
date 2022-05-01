// code by jph
package ch.alpine.sophus.sym;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.crv.bezier.BezierFunction;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;

class SymLinkBuilderTest {
  @Test
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
