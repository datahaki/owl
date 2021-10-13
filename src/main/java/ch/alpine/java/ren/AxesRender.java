// code by jph
package ch.alpine.java.ren;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** rendering of x and y axes with helper line */
public enum AxesRender implements RenderInterface {
  INSTANCE;

  private static final Tensor AXIS_X = Tensors.matrixInt(new int[][] { { -10, 0 }, { 10, 0 } });
  private static final Tensor AXIS_Y = Tensors.matrixInt(new int[][] { { 0, -10 }, { 0, 10 } });
  // ---
  private static final Tensor HELP_X = Tensors.matrixInt(new int[][] { { -10, 1 }, { 10, 1 } });
  private static final Tensor HELP_Y = Tensors.matrixInt(new int[][] { { 1, -10 }, { 1, 10 } });

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    {
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(HELP_X));
      graphics.draw(geometricLayer.toPath2D(HELP_Y));
    }
    {
      graphics.setColor(Color.GRAY);
      graphics.draw(geometricLayer.toPath2D(AXIS_X));
      graphics.draw(geometricLayer.toPath2D(AXIS_Y));
    }
  }
}
