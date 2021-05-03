// code by jph
package ch.alpine.owl.bot.r2;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.HeuristicQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class ImageCostFunctionTest extends TestCase {
  public void testSimple() {
    ImageCostFunction costFunction = //
        new DenseImageCostFunction(Tensors.fromString("{{1, 2}, {3, 4}}"), Tensors.vector(10, 10), RealScalar.ZERO);
    assertFalse(HeuristicQ.of(costFunction));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(1, 1)), RealScalar.of(3));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(9, 9)), RealScalar.of(2));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(1, 9)), RealScalar.of(1));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(9, 1)), RealScalar.of(4));
  }

  public void testSerializable() throws Exception {
    CostFunction costFunction = //
        new DenseImageCostFunction(Tensors.fromString("{{1, 2}, {3, 4}}"), Tensors.vector(10, 10), RealScalar.ZERO);
    Serialization.copy(costFunction);
  }
}
