// code by jph
package ch.alpine.owl.bot.se2.glc;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class ClothoidPursuitControlTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Clip clip = Serialization.copy(Clips.absolute(Quantity.of(2, "m^-1")));
    clip.requireInside(Quantity.of(-2, "m^-1"));
    clip.requireInside(Quantity.of(+2, "m^-1"));
    assertFalse(clip.isInside(Quantity.of(-3, "m^-1")));
    assertFalse(clip.isInside(Quantity.of(+3, "m^-1")));
  }
}
