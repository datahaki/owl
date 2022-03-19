// code by jph
package ch.alpine.java.ren;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.mat.IdentityMatrix;

public class AxesRenderTest {
  @Test
  public void testSimple() {
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    AxesRender.INSTANCE.render( //
        new GeometricLayer(IdentityMatrix.of(3)), bi.createGraphics());
  }
}
