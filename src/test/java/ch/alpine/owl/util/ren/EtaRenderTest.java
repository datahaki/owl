// code by jph
package ch.alpine.owl.util.ren;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.mat.IdentityMatrix;

class EtaRenderTest {
  @Test
  void testSimple() {
    EtaRender etaRender = new EtaRender(Tensors.vector(1, 2));
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    etaRender.render(new GeometricLayer(IdentityMatrix.of(3)), bi.createGraphics());
  }

  @Test
  void testSingle() {
    EtaRender etaRender = new EtaRender(Tensors.vector(1));
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    etaRender.render(new GeometricLayer(IdentityMatrix.of(3)), bi.createGraphics());
  }
}
