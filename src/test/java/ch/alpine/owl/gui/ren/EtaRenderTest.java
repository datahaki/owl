// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.mat.IdentityMatrix;

public class EtaRenderTest {
  @Test
  public void testSimple() {
    EtaRender etaRender = new EtaRender(Tensors.vector(1, 2));
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    etaRender.render(new GeometricLayer(IdentityMatrix.of(3)), bi.createGraphics());
  }

  @Test
  public void testSingle() {
    EtaRender etaRender = new EtaRender(Tensors.vector(1));
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    etaRender.render(new GeometricLayer(IdentityMatrix.of(3)), bi.createGraphics());
  }
}
