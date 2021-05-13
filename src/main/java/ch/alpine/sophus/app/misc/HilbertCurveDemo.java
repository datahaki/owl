// code by jph
package ch.alpine.sophus.app.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.function.Function;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.ren.PointsRender;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.ply.d2.HilbertPolygon;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.sca.Power;

/* package */ class HilbertCurveDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict();

  public static Tensor curve(int n) {
    System.out.println("compute here");
    return HilbertPolygon.of(n).multiply(Power.of(2.0, -n));
  }

  private final SpinnerLabel<Integer> spinnerTotal = new SpinnerLabel<>();
  private final Function<Integer, Tensor> cache = Cache.of(HilbertCurveDemo::curve, 10);

  public HilbertCurveDemo() {
    super(false, ManifoldDisplays.R2_ONLY);
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    spinnerTotal.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
    spinnerTotal.setValue(2);
    spinnerTotal.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "total");
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    int n = spinnerTotal.getValue();
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
