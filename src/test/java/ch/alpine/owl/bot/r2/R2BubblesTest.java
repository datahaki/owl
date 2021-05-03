// code by jph
package ch.alpine.owl.bot.r2;

import java.io.IOException;

import ch.alpine.owl.math.region.ImplicitFunctionRegion;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class R2BubblesTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    ImplicitFunctionRegion copy = Serialization.copy(R2Bubbles.INSTANCE);
    assertFalse(copy.isMember(Tensors.vector(1, 2)));
  }
}
