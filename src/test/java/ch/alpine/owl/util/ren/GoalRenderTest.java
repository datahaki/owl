// code by jph
package ch.alpine.owl.util.ren;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.mat.IdentityMatrix;

class GoalRenderTest {
  @Test
  void testSimple() {
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    GoalRender goalRender = new GoalRender(Arrays.asList(new StateTime(Array.zeros(2), RealScalar.ONE)));
    Graphics2D graphics = bi.createGraphics();
    goalRender.render(new GeometricLayer(IdentityMatrix.of(3)), graphics);
    graphics.dispose();
  }

  @Test
  void testNull() {
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    GoalRender goalRender = new GoalRender(null);
    Graphics2D graphics = bi.createGraphics();
    goalRender.render(new GeometricLayer(IdentityMatrix.of(3)), graphics);
    graphics.dispose();
  }
}
