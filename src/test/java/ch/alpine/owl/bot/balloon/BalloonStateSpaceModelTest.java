// code by astoll
package ch.alpine.owl.bot.balloon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;

class BalloonStateSpaceModelTest {
  @Test
  public void testValidity() throws ClassNotFoundException, IOException {
    Tensor xWithUnits = Tensors.fromString("{2[m], 2[m], 2[m*s^-1], 4[K]}");
    Tensor uWithUnits = Tensors.fromString("{3[K*s^-1]}");
    Tensor xWithoutUnits = Tensors.vector(1, 1, 2, 4);
    Tensor uWithoutUnits = Tensors.vector(2);
    StateSpaceModel stateSpaceModel = Serialization.copy(BalloonStateSpaceModels.defaultWithUnits());
    StateSpaceModel stateSpaceModelWithoutUnits = BalloonStateSpaceModels.defaultWithoutUnits();
    assertEquals(stateSpaceModelWithoutUnits.f(xWithoutUnits, uWithoutUnits).length(), 4);
    Tensor fWithUnits = stateSpaceModel.f(xWithUnits, uWithUnits);
    assertEquals(fWithUnits.length(), 4);
    assertEquals(QuantityUnit.of(fWithUnits.Get(0)), Unit.of("m*s^-1"));
    assertEquals(QuantityUnit.of(fWithUnits.Get(1)), Unit.of("m*s^-1"));
    assertEquals(QuantityUnit.of(fWithUnits.Get(2)), Unit.of("m*s^-2"));
    assertEquals(QuantityUnit.of(fWithUnits.Get(3)), Unit.of("K*s^-1"));
  }
}
