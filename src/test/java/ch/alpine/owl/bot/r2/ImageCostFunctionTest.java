// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.HeuristicQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class ImageCostFunctionTest {
  @Test
  void testSimple() {
    ImageCostFunction costFunction = //
        new DenseImageCostFunction(Tensors.fromString("{{1, 2}, {3, 4}}"), Tensors.vector(10, 10), RealScalar.ZERO);
    assertFalse(HeuristicQ.of(costFunction));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(1, 1)), RealScalar.of(3));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(9, 9)), RealScalar.of(2));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(1, 9)), RealScalar.of(1));
    assertEquals(costFunction.flipYXTensorInterp.at(Tensors.vector(9, 1)), RealScalar.of(4));
  }

  @Test
  void testSerializable() throws Exception {
    CostFunction costFunction = //
        new DenseImageCostFunction(Tensors.fromString("{{1, 2}, {3, 4}}"), Tensors.vector(10, 10), RealScalar.ZERO);
    Serialization.copy(costFunction);
  }
}
