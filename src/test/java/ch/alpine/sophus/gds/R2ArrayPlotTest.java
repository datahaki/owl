// code by jph
package ch.alpine.sophus.gds;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class R2ArrayPlotTest extends TestCase {
  public void testSerialization() throws ClassNotFoundException, IOException {
    Serialization.copy(new R2ArrayPlot(RealScalar.of(3)));
  }
}
