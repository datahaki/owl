// code by jph
package ch.alpine.sophus.app.clt;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.win.BaseFrame;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.clt.LagrangeQuadraticD;
import ch.alpine.sophus.gds.Se2ClothoidDisplay;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.ren.PointsRender;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.sophus.math.ArcTan2D;
import ch.alpine.sophus.ply.Arrowhead;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.sca.Round;

/** The demo shows that when using LaneRiesenfeldCurveSubdivision(Clothoid.INSTANCE, degree)
 * in order to connect two points p and q, then the (odd) degree has little influence on the
 * resulting curve. The difference is only noticeable for S shaped curves.
 * 
 * Therefore, for simplicity in algorithms we use degree == 1. */
/* package */ class ClothoidDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 100);
  private static final Tensor ARROWS = Subdivide.of(0.0, 1.0, 10);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = //
      ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final PointsRender POINTS_RENDER = //
      new PointsRender(new Color(0, 0, 0, 0), new Color(128, 128, 128, 64));

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
    int index = 0;
    for (ClothoidBuilders clothoidBuilders : ClothoidBuilders.values()) {
      Clothoid clothoid = clothoidBuilders.clothoidBuilder().curve(START, mouse);
      Tensor points = DOMAIN.map(clothoid);
      Color color = COLOR_DATA_INDEXED.getColor(index);
      new PathRender(color, 1.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
      POINTS_RENDER.show(Se2ClothoidDisplay.ANALYTIC::matrixLift, Arrowhead.of(0.3), ARROWS.map(clothoid)) //
          .render(geometricLayer, graphics);
      ++index;
      graphics.setColor(color);
      {
        Scalar angle = ArcTan2D.of(clothoid.apply(RealScalar.of(1e-8)));
        graphics.drawString(angle.map(Round._5) + "  " + clothoid.toString(), 0, index * 20);
        graphics.draw(geometricLayer.toLine2D(AngleVector.of(angle)));
      }
      {
        LagrangeQuadraticD lagrangeQuadraticD = clothoid.curvature();
        Scalar angle = lagrangeQuadraticD.head();
        graphics.draw(geometricLayer.toLine2D(AngleVector.of(angle).multiply(RealScalar.of(2))));
      }
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new ClothoidDemo().setVisible(1000, 600);
  }
}
