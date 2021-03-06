// code by jph
package ch.alpine.sophus.app.clt;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.BaseFrame;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.gds.Se2ClothoidDisplay;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.ren.PointsRender;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.sophus.ply.Arrowhead;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.sophus.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.red.Nest;

/** The demo shows that when using LaneRiesenfeldCurveSubdivision(Clothoid.INSTANCE, degree)
 * in order to connect two points p and q, then the (odd) degree has little influence on the
 * resulting curve. The difference is only noticeable for S shaped curves.
 * 
 * Therefore, for simplicity in algorithms we use degree == 1. */
/* package */ class ClothoidLRDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 100);
  private static final Tensor ARROWS = Subdivide.of(0.0, 1.0, 8);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final PointsRender POINTS_RENDER_C = new PointsRender(new Color(0, 0, 0, 0), new Color(128, 128, 128, 64));
  private static final PointsRender POINTS_RENDER_S = new PointsRender(new Color(0, 0, 0), Color.BLACK);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    {
      graphics.setColor(new Color(255, 0, 0, 128));
      geometricLayer.pushMatrix(Se2Matrix.of(mouse));
      graphics.fill(geometricLayer.toPath2D(Arrowhead.of(0.3)));
      geometricLayer.popMatrix();
    }
    int index = 0;
    for (ClothoidBuilder clothoidBuilder : new ClothoidBuilder[] { //
        ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder(), ClothoidBuilders.SE2_LEGENDRE.clothoidBuilder() }) {
      Clothoid clothoid = clothoidBuilder.curve(START, mouse);
      Tensor points = DOMAIN.map(clothoid);
      new PathRender(COLOR_DATA_INDEXED.getColor(index), 1.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
      POINTS_RENDER_C.show(Se2ClothoidDisplay.ANALYTIC::matrixLift, Arrowhead.of(0.3), ARROWS.map(clothoid)) //
          .render(geometricLayer, graphics);
      ++index;
    }
    {
      CurveSubdivision curveSubdivision = LaneRiesenfeldCurveSubdivision.of(ClothoidBuilders.SE2_LEGENDRE.clothoidBuilder(), 1);
      Tensor points = Nest.of(curveSubdivision::string, Tensors.of(START, mouse), 2); // length == 129
      new PathRender(COLOR_DATA_INDEXED.getColor(2), 2.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
      POINTS_RENDER_S.show(Se2ClothoidDisplay.ANALYTIC::matrixLift, Arrowhead.of(0.3), points) //
          .render(geometricLayer, graphics);
    }
    {
      CurveSubdivision curveSubdivision = LaneRiesenfeldCurveSubdivision.of(ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder(), 1);
      Tensor points = Nest.of(curveSubdivision::string, Tensors.of(START, mouse), 2); // length == 129
      new PathRender(COLOR_DATA_INDEXED.getColor(2), 2.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
      POINTS_RENDER_S.show(Se2ClothoidDisplay.ANALYTIC::matrixLift, Arrowhead.of(0.3), points) //
          .render(geometricLayer, graphics);
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new ClothoidLRDemo().setVisible(1000, 600);
  }
}
