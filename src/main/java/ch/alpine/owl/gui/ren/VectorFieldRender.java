// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.RenderInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** suitable for time-variant state space models */
public class VectorFieldRender implements RenderInterface {
  private static final Color COLOR = new Color(192, 192, 192, 128);
  // ---
  // TODO JPH very bad design, not thread safe, etc.
  public Tensor uv_pairs = Tensors.empty();

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(COLOR);
    for (Tensor pair : uv_pairs)
      graphics.draw(new Line2D.Double( //
          geometricLayer.toPoint2D(pair.get(0)), //
          geometricLayer.toPoint2D(pair.get(1))));
  }
}
