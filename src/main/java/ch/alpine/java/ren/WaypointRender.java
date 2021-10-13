// code by ynager, jph
package ch.alpine.java.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** draws a given shape at frames */
public class WaypointRender implements RenderInterface {
  private final Tensor shape;
  private final Color color;
  // ---
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  /** @param shape with dimensions m x 2
   * @param color */
  public WaypointRender(Tensor shape, Color color) {
    this.shape = shape;
    this.color = color;
  }

  /** @param frames with dimensions n x 3 where each row corresponds to {x, y, angle}
   * @return */
  public RenderInterface setWaypoints(Tensor waypoints) {
    return renderInterface = Objects.isNull(waypoints) || Tensors.isEmpty(waypoints) //
        ? EmptyRender.INSTANCE
        : new Render(waypoints);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderInterface.render(geometricLayer, graphics);
  }

  private class Render implements RenderInterface {
    private final Tensor se2Matrixs;

    public Render(Tensor waypoints) {
      se2Matrixs = Tensor.of(waypoints.stream().map(GfxMatrix::of));
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      graphics.setColor(color);
      for (Tensor se2Matrix : se2Matrixs) { // draw frame as arrow
        geometricLayer.pushMatrix(se2Matrix);
        graphics.fill(geometricLayer.toPath2D(shape));
        geometricLayer.popMatrix();
      }
    }
  }
}
