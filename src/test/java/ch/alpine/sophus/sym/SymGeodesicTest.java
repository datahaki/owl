// code by jph
package ch.alpine.sophus.sym;

import java.util.stream.IntStream;

import ch.alpine.sophus.flt.ga.GeodesicCenter;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.win.WindowFunctions;
import junit.framework.TestCase;

public class SymGeodesicTest extends TestCase {
  public void testSimple() {
    Scalar s1 = SymScalar.leaf(1);
    Scalar s2 = SymScalar.leaf(2);
    SymScalar s3 = (SymScalar) SymScalar.of(s1, s2, RationalScalar.HALF);
    Scalar scalar = SymScalar.of(s1, s2, RationalScalar.of(1, 2));
    assertEquals(s3, scalar);
    Scalar evaluate = s3.evaluate();
    assertEquals(evaluate, RationalScalar.of(3, 2));
    TensorUnaryOperator tensorUnaryOperator = //
        GeodesicCenter.of(SymGeodesic.INSTANCE, WindowFunctions.DIRICHLET.get());
    Tensor vector = Tensor.of(IntStream.range(0, 5).mapToObj(SymScalar::leaf));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    assertEquals(tensor.toString(), "{{{0, 1, 1/2}, 2, 1/5}, {2, {3, 4, 1/2}, 4/5}, 1/2}");
    SymLink root = SymLink.build((SymScalar) tensor);
    Tensor pose = root.getPosition();
    assertEquals(pose, Tensors.vector(2, -1.5));
    SymScalar res = (SymScalar) tensor;
    assertEquals(res.evaluate(), RealScalar.of(2));
  }
}
