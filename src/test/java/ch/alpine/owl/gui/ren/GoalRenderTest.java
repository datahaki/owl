// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.mat.IdentityMatrix;

class GoalRenderTest {
  @Test
  public void testSimple() {
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    GoalRender goalRender = new GoalRender(Arrays.asList(new StateTime(Array.zeros(2), RealScalar.ONE)));
    goalRender.render(new GeometricLayer(IdentityMatrix.of(3)), bi.createGraphics());
  }

  @Test
  public void testNull() {
    BufferedImage bi = ImageFormat.of(Array.zeros(100, 100, 4));
    GoalRender goalRender = new GoalRender(null);
    goalRender.render(new GeometricLayer(IdentityMatrix.of(3)), bi.createGraphics());
  }
}
