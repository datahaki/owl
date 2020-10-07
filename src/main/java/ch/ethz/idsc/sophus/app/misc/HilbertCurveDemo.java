// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Objects;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.hs.r2.HilbertCurve;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.qty.LruCache;

/* package */ class HilbertCurveDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict();
  private final SpinnerLabel<Integer> spinnerTotal = new SpinnerLabel<>();
  private final LruCache<Integer, Tensor> lruCache = new LruCache<>(10);

  public HilbertCurveDemo() {
    super(false, GeodesicDisplays.R2_ONLY);
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
    Tensor tensor = lruCache.get(n);
    if (Objects.isNull(tensor))
      lruCache.put(n, tensor = HilbertCurve.closed(n));
    Path2D path2d = geometricLayer.toPath2D(tensor);
    graphics.setColor(new Color(128, 128, 128, 64));
    graphics.fill(path2d);
    new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f).setCurve(tensor, true).render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    {
      PointsRender pointsRender = new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
      pointsRender.show(geodesicDisplay::matrixLift, geodesicDisplay.shape(), tensor).render(geometricLayer, graphics);
    }
    if (0 < tensor.length()) {
      PointsRender pointsRender = new PointsRender(new Color(255, 128, 128, 64), Color.BLACK);
      pointsRender.show(geodesicDisplay::matrixLift, geodesicDisplay.shape(), tensor.extract(0, 1)).render(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new HilbertCurveDemo().setVisible(1000, 600);
  }
}
