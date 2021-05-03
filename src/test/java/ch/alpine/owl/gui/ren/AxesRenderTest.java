// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.image.BufferedImage;

import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class AxesRenderTest extends TestCase {
  public void testSimple() {
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    AxesRender.INSTANCE.render( //
        new GeometricLayer(IdentityMatrix.of(3), Array.zeros(3)), bi.createGraphics());
  }
}
