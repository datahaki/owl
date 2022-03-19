// code by jph
package ch.alpine.owl.bot.kl;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;

public class KlotskiFlowsTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Serialization.copy(new KlotskiFlows(Huarong.AMBUSH.create()));
  }
}
