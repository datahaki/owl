// code by jph
package ch.alpine.owl.ani.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.ani.api.EntityControl;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

public class FallbackControlTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    EntityControl fallbackControl = Serialization.copy(FallbackControl.of(Tensors.vector(1, 2, 3)));
    Optional<Tensor> optional = fallbackControl.control(null, null);
    assertEquals(optional.get(), Tensors.vector(1, 2, 3));
  }
}
