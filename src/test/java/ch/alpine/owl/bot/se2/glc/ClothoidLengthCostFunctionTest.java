// code by jph
package ch.alpine.owl.bot.se2.glc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class ClothoidLengthCostFunctionTest {
  @Test
  public void testSimple() {
    ClothoidLengthCostFunction clothoidLengthCostFunction = new ClothoidLengthCostFunction(s -> true);
    Scalar scalar = clothoidLengthCostFunction.apply(Tensors.vector(1, 2, 3));
    Chop._03.requireClose(scalar, RealScalar.of(3.3833822797708275));
  }

  @Test
  public void testUnits() {
    ClothoidLengthCostFunction clothoidLengthCostFunction = new ClothoidLengthCostFunction(s -> true);
    Scalar scalar = clothoidLengthCostFunction.apply(Tensors.fromString("{1[m], 2[m], 3}"));
    Chop._03.requireClose(scalar, Quantity.of(3.3833822797708275, "m"));
  }

  @Test
  public void testInfinite() {
    {
      ClothoidLengthCostFunction clothoidLengthCostFunction = new ClothoidLengthCostFunction(s -> false);
      Scalar scalar = clothoidLengthCostFunction.apply(Tensors.vector(1, 2, 3));
      assertEquals(DoubleScalar.POSITIVE_INFINITY, scalar);
    }
    {
      ClothoidLengthCostFunction clothoidLengthCostFunction = new ClothoidLengthCostFunction(s -> false);
      Scalar scalar = clothoidLengthCostFunction.apply(Tensors.fromString("{1[m], 2[m], 3}"));
      assertEquals(Quantity.of(DoubleScalar.POSITIVE_INFINITY, "m"), scalar);
    }
  }
}
