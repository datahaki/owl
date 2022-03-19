// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.PathRender;
import ch.alpine.java.ren.PointsRender;
import ch.alpine.java.ren.RenderInterface;
import ch.alpine.sophus.crv.d2.Arrowhead;
import ch.alpine.sophus.ext.api.ControlPointsDemo;
import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.sophus.ext.dis.Se2ClothoidDisplay;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;

/* package */ abstract class AbstractSpiralDemo extends ControlPointsDemo {
  private static final PointsRender POINTS_RENDER = //
      new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 128));
  private static final Tensor SEPARATORS = Subdivide.of(-3.0, 3.0, 50);
  // ---
  private final ScalarTensorFunction scalarTensorFunction;
  private final RenderInterface renderInterface;

  public AbstractSpiralDemo(ScalarTensorFunction scalarTensorFunction) {
    super(false, ManifoldDisplays.R2_ONLY);
    this.scalarTensorFunction = scalarTensorFunction;
    Tensor points = Subdivide.of(-10.0, 10.0, 10000).map(scalarTensorFunction);
    renderInterface = new PathRender(Color.BLUE, 1f).setCurve(points, false);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderInterface.render(geometricLayer, graphics);
    Tensor points = SEPARATORS.map(scalarTensorFunction);
    POINTS_RENDER.show(Se2ClothoidDisplay.ANALYTIC::matrixLift, Arrowhead.of(0.03), points) //
        .render(geometricLayer, graphics);
  }
}
