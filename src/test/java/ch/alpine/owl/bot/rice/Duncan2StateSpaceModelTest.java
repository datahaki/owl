// code by jph
package ch.alpine.owl.bot.rice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

public class Duncan2StateSpaceModelTest {
  @Test
  public void testScalar() {
    StateSpaceModel stateSpaceModel = new Duncan2StateSpaceModel(Quantity.of(0.1, "s^-1"));
    Tensor x = Tensors.fromString("{10[m], 5[m*s^-1]}");
    Tensor u = Tensors.fromString("{-1[m*s^-2]}");
    Tensor x_fxu = x.add(stateSpaceModel.f(x, u).multiply(Quantity.of(1, "s")));
    assertEquals(x_fxu, Tensors.fromString("{15[m], 3.5[m*s^-1]}"));
  }

  @Test
  public void testZero() {
    StateSpaceModel stateSpaceModel = new Duncan2StateSpaceModel(Quantity.of(0, "s^-1"));
    Tensor x = Tensors.fromString("{10[m], 5[m*s^-1]}");
    Tensor u = Tensors.fromString("{-1[m*s^-2]}");
    Tensor x_fxu = x.add(stateSpaceModel.f(x, u).multiply(Quantity.of(1, "s")));
    assertEquals(x_fxu, Tensors.fromString("{15[m], 4[m*s^-1]}"));
  }

  @Test
  public void testFail() {
    assertThrows(Exception.class, () -> new Duncan2StateSpaceModel(Quantity.of(-1.0, "s^-1")));
  }
}
