// code by jph
package ch.alpine.sophus.ext.dis;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.arp.R2ArrayPlot;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;

public class R2ArrayPlotTest {
  @Test
  public void testSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(new R2ArrayPlot(RealScalar.of(3)));
  }
}
