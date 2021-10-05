// code by jph
package ch.alpine.sophus.demo.clt;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.PathRender;
import ch.alpine.java.win.AbstractDemo;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.clt.LagrangeQuadraticD;
import ch.alpine.sophus.crv.d2.Arrowhead;
import ch.alpine.sophus.lie.se2.Se2GroupElement;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.sca.Exp;

/** The demo shows that when using LaneRiesenfeldCurveSubdivision(Clothoid.INSTANCE, degree)
 * in order to connect two points p and q, then the (odd) degree has little influence on the
 * resulting curve. The difference is only noticeable for S shaped curves.
 * 
 * Therefore, for simplicity in algorithms we use degree == 1. */
/* package */ class ClothoidStrokeDemo extends AbstractDemo {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 100);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = //
      ColorDataLists._097.cyclic().deriveWithAlpha(192);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor mouse = timerFrame.geometricComponent.getMouseSe2CState();
    // ---
    {
      graphics.setColor(new Color(255, 0, 0, 128));
      geometricLayer.pushMatrix(GfxMatrix.of(mouse));
      graphics.fill(geometricLayer.toPath2D(Arrowhead.of(0.3)));
      geometricLayer.popMatrix();
    }
    ClothoidBuilder clothoidBuilder = ClothoidBuilders.SE2_COVERING.clothoidBuilder();
    {
      Clothoid clothoid = clothoidBuilder.curve(START, mouse);
      Tensor points = DOMAIN.map(clothoid);
      Color color = COLOR_DATA_INDEXED.getColor(0);
      new PathRender(color, 1.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
      LagrangeQuadraticD lagrangeQuadraticD = clothoid.curvature();
      Tensor above = Tensors.empty();
      Tensor below = Tensors.empty();
      for (Tensor _t : DOMAIN) {
        Scalar t = (Scalar) _t;
        Se2GroupElement se2GroupElement = new Se2GroupElement(clothoid.apply(t));
        Scalar curvature = lagrangeQuadraticD.apply(t);
        Scalar radius = Exp.FUNCTION.apply(curvature.multiply(curvature).negate());
        above.append(se2GroupElement.combine(Tensors.of(radius.zero(), radius, RealScalar.ZERO)));
        below.append(se2GroupElement.combine(Tensors.of(radius.zero(), radius.negate(), RealScalar.ZERO)));
      }
      new PathRender(color, 1.5f) //
          .setCurve(above, false).render(geometricLayer, graphics);
      new PathRender(color, 1.5f) //
          .setCurve(below, false).render(geometricLayer, graphics);
      Tensor tensor = Join.of(above, Reverse.of(below));
      graphics.fill(geometricLayer.toPath2D(tensor));
    }
  }

  public static void main(String[] args) {
    new ClothoidStrokeDemo().setVisible(1000, 600);
  }
}
