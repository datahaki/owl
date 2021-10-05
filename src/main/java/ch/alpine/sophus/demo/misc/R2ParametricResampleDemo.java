// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.gui.PanelFieldsEditor;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.PointsRender;
import ch.alpine.sophus.crv.d2.ParametricResample;
import ch.alpine.sophus.crv.d2.ResampleResult;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.lie.r2.CirclePoints;

public class R2ParametricResampleDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict().deriveWithAlpha(128);
  private static final PointsRender POINTS_RENDER = new PointsRender(new Color(0, 128, 128, 64), new Color(0, 128, 128, 255));
  // ---
  @FieldClip(min = "0", max = "10")
  public Scalar threshold = RealScalar.of(3);
  public Scalar ds = RealScalar.of(0.3);

  public R2ParametricResampleDemo() {
    super(true, ManifoldDisplays.R2_ONLY);
    // ---
    Container container = timerFrame.jFrame.getContentPane();
    PanelFieldsEditor fieldsPanel = new PanelFieldsEditor(this);
    container.add("West", fieldsPanel.getJScrollPane());
    // ---
    int n = 20;
    setControlPointsSe2(PadRight.zeros(n, 3).apply(CirclePoints.of(n).multiply(RealScalar.of(3))));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
    graphics.setStroke(new BasicStroke(2f));
    graphics.draw(geometricLayer.toPath2D(control));
    renderControlPoints(geometricLayer, graphics);
    // ---
    ParametricResample parametricResample = new ParametricResample(threshold, ds);
    ResampleResult resampleResult = parametricResample.apply(control);
    for (Tensor points : resampleResult.getPoints())
      POINTS_RENDER.show(manifoldDisplay()::matrixLift, manifoldDisplay().shape(), points) //
          .render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new R2ParametricResampleDemo().setVisible(1000, 600);
  }
}
