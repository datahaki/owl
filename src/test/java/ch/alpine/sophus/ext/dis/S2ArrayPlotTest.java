// code by jph
package ch.alpine.sophus.ext.dis;

import java.io.IOException;

import ch.alpine.sophus.ext.arp.S2ArrayPlot;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class S2ArrayPlotTest extends TestCase {
  public void testSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(S2ArrayPlot.INSTANCE);
  }
}
