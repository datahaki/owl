// code by jph
package ch.alpine.owl.ani.adapter;

import java.io.IOException;
import java.util.Optional;

import ch.alpine.owl.ani.api.EntityControl;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class FallbackControlTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    EntityControl fallbackControl = Serialization.copy(FallbackControl.of(Tensors.vector(1, 2, 3)));
    Optional<Tensor> optional = fallbackControl.control(null, null);
    assertEquals(optional.get(), Tensors.vector(1, 2, 3));
  }
}
