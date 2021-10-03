// code by jph
package ch.alpine.sophus.app.dubins;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.BaseFrame;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.crv.dubins.DubinsPath;
import ch.alpine.sophus.crv.dubins.DubinsPathComparators;
import ch.alpine.sophus.crv.dubins.DubinsPathGenerator;
import ch.alpine.sophus.crv.dubins.FixedRadiusDubins;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.Se2CoveringDisplay;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.ren.PointsRender;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;

/* package */ class SingleDubinsPathDemo extends AbstractDemo implements DemoInterface {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final int POINTS = 200;
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  protected static final PointsRender POINTS_RENDER = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  // ---
  private final PathRender pathRender = new PathRender(Color.RED, 2f);
  private final PathRender pathRenderClothoid = new PathRender(Color.CYAN, 2f);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor mouse = timerFrame.geometricComponent.getMouseSe2CState();
    {
      ManifoldDisplay manifoldDisplay = Se2CoveringDisplay.INSTANCE;
      POINTS_RENDER.show(manifoldDisplay::matrixLift, manifoldDisplay.shape(), Tensors.of(START, mouse)) //
          .render(geometricLayer, graphics);
    }
    // ---
    DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(START, mouse, RealScalar.of(1));
    List<DubinsPath> list = dubinsPathGenerator.stream().collect(Collectors.toList());
    {
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.setStroke(new BasicStroke(1f));
      // for (DubinsPath dubinsPath : list)
      // graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
    { // draw shortest path
      graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
      graphics.setStroke(new BasicStroke(2f));
      // DubinsPath dubinsPath = list.stream().min(DubinsPathComparators.LENGTH).get();
      // graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
    {
      // DubinsPath dubinsPath = list.stream().min(DubinsPathComparators.LENGTH).get();
      // ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(START);
      // Tensor params = PadLeft.zeros(4).apply(dubinsPath.segments());
      // graphics.setColor(new Color(128, 128, 128, 128));
      // // graphics.setColor(COLOR_DATA_INDEXED.getColor(3));
      // Tensor map = params.map(scalarTensorFunction);
      // for (Tensor point : map) { // draw control point
      // geometricLayer.pushMatrix(Se2CoveringDisplay.INSTANCE.matrixLift(point));
      // Path2D path2d = geometricLayer.toPath2D(Se2CoveringDisplay.INSTANCE.shape());
      // graphics.fill(path2d);
      // geometricLayer.popMatrix();
      // }
      // BSpline3CurveSubdivision bSpline3CurveSubdivision = //
      // new BSpline3CurveSubdivision(Se2CoveringGeodesic.INSTANCE);
      // Tensor points = Nest.of(bSpline3CurveSubdivision::string, map, 5);
      // // graphics.setStroke(new BasicStroke(2f));
      // pathRender.setCurve(points, false).render(geometricLayer, graphics);
    }
    { // draw clothoid
      // ClothoidTransition clothoidTransition = //
      // ClothoidTransition.of(CLOTHOID_BUILDER, START, mouse);
      // Tensor tensor = clothoidTransition.linearized(RealScalar.of(0.1));
      // pathRenderClothoid.setCurve(tensor, false).render(geometricLayer, graphics);
      // TODO
    }
    { // draw least curved path
      graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
      graphics.setStroke(new BasicStroke(2f));
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparators.TOTAL_CURVATURE).orElseThrow();
      graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
  }

  private static Tensor sample(DubinsPath dubinsPath) {
    return Subdivide.of(0.0, 1.0, POINTS).map(dubinsPath.unit(START));
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new SingleDubinsPathDemo().setVisible(1000, 600);
  }
}
