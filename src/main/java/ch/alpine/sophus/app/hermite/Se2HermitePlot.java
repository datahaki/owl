// code by jph
package ch.alpine.sophus.app.hermite;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.Hsluv;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.Cross;

/* package */ class Se2HermitePlot implements RenderInterface {
  private final Tensor points;
  private final Scalar scale;

  /** @param points with dimensions N x 2 x 3
   * @param scale */
  public Se2HermitePlot(Tensor points, Scalar scale) {
    this.points = points;
    this.scale = scale;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    for (Tensor point : points) {
      geometricLayer.pushMatrix(GfxMatrix.of(point.get(0)));
      Tensor pv = point.get(1);
      Color color = Hsluv.of(pv.Get(2).number().doubleValue() * 0.3, 1, 0.5, 0.5);
      graphics.setColor(color);
      Tensor vec = Cross.of(pv.extract(0, 2).multiply(scale));
      graphics.draw(geometricLayer.toLine2D(vec));
      geometricLayer.popMatrix();
    }
  }
}
