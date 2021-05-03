// code by jph
package ch.alpine.sophus.gui.ren;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.Objects;

import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.ren.EmptyRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class PathRender implements RenderInterface {
  private static final Stroke DEFAULT = new BasicStroke(1f);
  // ---
  private final Color color;
  private final Stroke stroke;
  // ---
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  /** @param color
   * @param stroke */
  public PathRender(Color color, Stroke stroke) {
    this.color = color;
    this.stroke = stroke;
  }

  /** @param color
   * @param width of stroke */
  public PathRender(Color color, float width) {
    this(color, new BasicStroke(width));
  }

  public PathRender(Color color) {
    this(color, DEFAULT);
  }

  /** @param points {{p1x, p1y, [...]}, {p2x, p2y, [...]}, ..., {pNx, pNy, [...]}}
   * @param cyclic
   * @return */
  public RenderInterface setCurve(Tensor points, boolean cyclic) {
    return renderInterface = Objects.isNull(points) || Tensors.isEmpty(points) //
        ? EmptyRender.INSTANCE
        : new Render(points, cyclic);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderInterface.render(geometricLayer, graphics);
  }

  private class Render implements RenderInterface {
    private final Tensor points;
    private final boolean cyclic;

    public Render(Tensor points, boolean cyclic) {
      this.points = points;
      this.cyclic = cyclic;
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      Path2D path2d = geometricLayer.toPath2D(points);
      if (cyclic)
        path2d.closePath();
      graphics.setStroke(stroke);
      graphics.setColor(color);
      graphics.draw(path2d);
      graphics.setStroke(DEFAULT);
    }
  }
}
