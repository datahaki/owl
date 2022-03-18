// code by jph
package ch.alpine.sophus.ext.dis;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.arp.S2ArrayPlot;
import ch.alpine.tensor.ext.Serialization;

public class S2ArrayPlotTest {
  @Test
  public void testSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(S2ArrayPlot.INSTANCE);
  }
}
