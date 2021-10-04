// code by jph
package ch.alpine.sophus.demo.ref.d1h;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.Cross;

/* package */ class R2HermitePlot implements RenderInterface {
  private final Tensor points;
  private final Scalar scale;

  public R2HermitePlot(Tensor points, Scalar scale) {
    this.points = points;
    this.scale = scale;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(new Color(128, 128, 128, 128));
    for (Tensor point : points) {
      Tensor pg = point.get(0);
      Tensor vec = Cross.of(point.get(1).multiply(scale));
      graphics.draw(geometricLayer.toLine2D(pg, pg.add(vec)));
    }
  }
}
