// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.io.IOException;

import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidPursuitControlTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Clip clip = Serialization.copy(Clips.absolute(Quantity.of(2, "m^-1")));
    clip.requireInside(Quantity.of(-2, "m^-1"));
    clip.requireInside(Quantity.of(+2, "m^-1"));
    assertFalse(clip.isInside(Quantity.of(-3, "m^-1")));
    assertFalse(clip.isInside(Quantity.of(+3, "m^-1")));
  }
}
