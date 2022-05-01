// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.region.ImplicitFunctionRegion;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class R2BubblesTest {
  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    ImplicitFunctionRegion copy = Serialization.copy(R2Bubbles.INSTANCE);
    assertFalse(copy.test(Tensors.vector(1, 2)));
  }
}
