// code by jph
package ch.alpine.owl.bot.kl;

import java.io.IOException;

import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class KlotskiFlowsTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Serialization.copy(new KlotskiFlows(Huarong.AMBUSH.create()));
  }
}
