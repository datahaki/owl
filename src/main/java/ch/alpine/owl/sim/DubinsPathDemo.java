// code by jph
package ch.alpine.owl.sim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.alpine.ascona.dis.Se2CoveringDisplay;
import ch.alpine.bridge.awt.RenderQuality;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.util.ToolbarFieldsEditor;
import ch.alpine.bridge.win.AbstractDemo;
import ch.alpine.bridge.win.BaseFrame;
import ch.alpine.bridge.win.DemoInterface;
import ch.alpine.bridge.win.LookAndFeels;
import ch.alpine.bridge.win.PathRender;
import ch.alpine.owl.bot.se2.rrts.ClothoidTransition;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.crv.dubins.DubinsPath;
import ch.alpine.sophus.crv.dubins.DubinsPathComparators;
import ch.alpine.sophus.crv.dubins.DubinsPathGenerator;
import ch.alpine.sophus.crv.dubins.DubinsRadius;
import ch.alpine.sophus.crv.dubins.DubinsType;
import ch.alpine.sophus.crv.dubins.FixedRadiusDubins;
import ch.alpine.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.alpine.sophus.ref.d1.BSpline3CurveSubdivision;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.PadLeft;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Clips;

/* package */ class DubinsPathDemo extends AbstractDemo implements DemoInterface {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final int POINTS = 200;
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();

  public static class Param {
    public Boolean allDubins = true;
    public Boolean relax = true;
    public Boolean shortest = true;
    public Boolean clothoid = true;
  }

  private final Param param = new Param();
  private final PathRender pathRender = new PathRender(Color.RED, 2f);
  private final PathRender pathRenderClothoid = new PathRender(Color.CYAN, 2f);

  public DubinsPathDemo() {
    ToolbarFieldsEditor.add(param, timerFrame.jToolBar);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor mouse = timerFrame.geometricComponent.getMouseSe2CState();
    // ---
    DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(START, mouse, RealScalar.of(1));
    List<DubinsPath> list = dubinsPathGenerator.stream().collect(Collectors.toList());
    if (param.allDubins) {
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.setStroke(new BasicStroke(1f));
      if (param.relax) { // draw shortest path
        for (DubinsType dubinsType : DubinsType.values()) {
          Scalar maxRadius = DubinsRadius.getMax(mouse, dubinsType, Clips.interval(0.5, 2));
          if (FiniteScalarQ.of(maxRadius)) {
            Optional<DubinsPath> optional = FixedRadiusDubins.of(mouse, dubinsType, maxRadius);
            if (optional.isPresent()) {
              graphics.draw(geometricLayer.toPath2D(sample(optional.get())));
            }
          }
        }
      } else
        for (DubinsPath dubinsPath : list)
          graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
    if (param.shortest) { // draw shortest path
      graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
      graphics.setStroke(new BasicStroke(2f));
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparators.LENGTH).orElseThrow();
      graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
    {
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparators.LENGTH).orElseThrow();
      ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(START);
      Tensor params = PadLeft.zeros(4).apply(dubinsPath.segments());
      graphics.setColor(new Color(128, 128, 128, 128));
      // graphics.setColor(COLOR_DATA_INDEXED.getColor(3));
      Tensor map = params.map(scalarTensorFunction);
      for (Tensor point : map) { // draw control point
        geometricLayer.pushMatrix(Se2CoveringDisplay.INSTANCE.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(Se2CoveringDisplay.INSTANCE.shape());
        graphics.fill(path2d);
        geometricLayer.popMatrix();
      }
      BSpline3CurveSubdivision bSpline3CurveSubdivision = //
          new BSpline3CurveSubdivision(Se2CoveringGeodesic.INSTANCE);
      Tensor points = Nest.of(bSpline3CurveSubdivision::string, map, 5);
      // graphics.setStroke(new BasicStroke(2f));
      pathRender.setCurve(points, false).render(geometricLayer, graphics);
    }
    if (param.clothoid) { // draw clothoid
      ClothoidTransition clothoidTransition = //
          ClothoidTransition.of(CLOTHOID_BUILDER, START, mouse);
      Tensor tensor = clothoidTransition.linearized(RealScalar.of(0.1));
      pathRenderClothoid.setCurve(tensor, false).render(geometricLayer, graphics);
      // TODO OWL ALG
    }
    { // draw least curved path
      graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
      graphics.setStroke(new BasicStroke(2f));
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparators.TOTAL_CURVATURE).get();
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
    LookAndFeels.INTELLI_J.updateUI();
    new DubinsPathDemo().setVisible(1000, 600);
  }
}
