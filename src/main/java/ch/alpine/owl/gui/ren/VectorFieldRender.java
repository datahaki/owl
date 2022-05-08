// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import ch.alpine.ascona.util.ren.EmptyRender;
import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.tensor.Tensor;

/** suitable for time-variant state space models */
public class VectorFieldRender implements RenderInterface {
  private static final Color COLOR = new Color(192, 192, 192, 128);
  // ---
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderInterface.render(geometricLayer, graphics);
  }

  /** @param uv_pairs with dimensions n x 2 x 2
   * @return */
  public RenderInterface setUV_Pairs(Tensor uv_pairs) {
    return renderInterface = new Render(uv_pairs);
  }

  private class Render implements RenderInterface {
    private final Tensor uv_pairs;

    public Render(Tensor uv_pairs) {
      this.uv_pairs = uv_pairs;
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      graphics.setColor(COLOR);
      for (Tensor pair : uv_pairs)
        graphics.draw(new Line2D.Double( //
            geometricLayer.toPoint2D(pair.get(0)), //
            geometricLayer.toPoint2D(pair.get(1))));
    }
  }
}
