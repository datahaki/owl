// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.function.Function;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.PathRender;
import ch.alpine.java.ren.PointsRender;
import ch.alpine.sophus.crv.d2.HilbertPolygon;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.sca.Power;

public class HilbertCurveDemo extends ControlPointsDemo {
  private static final int CACHE_SIZE = 10;
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict();

  public static Tensor curve(int n) {
    return HilbertPolygon.of(n).multiply(Power.of(2.0, -n));
  }

  @FieldSlider
  @FieldInteger
  @FieldClip(min = "1", max = "7")
  public Scalar total = RealScalar.of(2);
  private final Function<Integer, Tensor> cache = Cache.of(HilbertCurveDemo::curve, CACHE_SIZE);

  public HilbertCurveDemo() {
    super(false, ManifoldDisplays.R2_ONLY);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    int n = total.number().intValue();
    Tensor tensor = cache.apply(n);
    // ---
    Path2D path2d = geometricLayer.toPath2D(tensor);
    graphics.setColor(new Color(128, 128, 128, 64));
    graphics.fill(path2d);
    new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f).setCurve(tensor, true).render(geometricLayer, graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    {
      PointsRender pointsRender = new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
      pointsRender.show(manifoldDisplay::matrixLift, manifoldDisplay.shape(), tensor).render(geometricLayer, graphics);
    }
    if (0 < tensor.length()) {
      PointsRender pointsRender = new PointsRender(new Color(255, 128, 128, 64), Color.BLACK);
      pointsRender.show(manifoldDisplay::matrixLift, manifoldDisplay.shape(), tensor.extract(0, 1)).render(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new HilbertCurveDemo().setVisible(1000, 600);
  }
}
