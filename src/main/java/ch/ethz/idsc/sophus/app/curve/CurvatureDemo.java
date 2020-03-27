// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.BufferedImageSupplier;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.CurveVisualSet;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;

/** class is used in other projects outside of owl */
public abstract class CurvatureDemo extends ControlPointsDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  // ---
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  public final JToggleButton jToggleCurvature = new JToggleButton("crvtp");

  public CurvatureDemo() {
    this(GeodesicDisplays.ALL);
  }

  public CurvatureDemo(List<GeodesicDisplay> list) {
    super(true, list);
    // ---
    jToggleCurvature.setSelected(true);
    jToggleCurvature.setToolTipText("curvature plot");
    timerFrame.jToolBar.add(jToggleCurvature);
    // ---
    if (this instanceof BufferedImageSupplier) {
      jToggleSymi.setSelected(true);
      timerFrame.jToolBar.add(jToggleSymi);
    }
  }

  @Override
  public synchronized final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor refined = protected_render(geometricLayer, graphics);
    if (this instanceof BufferedImageSupplier && jToggleSymi.isSelected()) {
      BufferedImageSupplier bufferedImageSupplier = (BufferedImageSupplier) this;
      graphics.drawImage(bufferedImageSupplier.bufferedImage(), 0, 0, null);
    }
    // ---
    Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    if (jToggleCurvature.isSelected() && 1 < refined.length()) {
      Tensor tensor = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
      CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
      curveVisualSet.addCurvature();
      // if (2 < refined.get(0).length())
      // curveVisualSet.addArcTan(refined);
      VisualSet visualSet = curveVisualSet.visualSet();
      {
        // {
        // Tensor domain = Range.of(0, phase.length());
        // VisualRow visualRow = visualSet.add(domain, phase);
        // visualRow.setLabel("phase diff");
        // visualRow.setStroke(PLOT_STROKE);
        // }
        // {
        // Tensor domain = Range.of(0, dpntlnXY.length());
        // VisualRow visualRow = visualSet.add(domain, dpntlnXY);
        // visualRow.setLabel("arclen");
        // visualRow.setStroke(PLOT_STROKE);
        // }
        // {
        // Tensor div = phase.pmul(dpntlnXY.map(InvertUnlessZero.FUNCTION));
        // Tensor domain = Range.of(0, div.length());
        // Tensor values = div.multiply(RealScalar.of(-1));
        // VisualRow visualRow = visualSet.add(domain, values);
        // visualRow.setLabel("phase/arclen");
        // visualRow.setStroke(PLOT_STROKE);
        // }
      }
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    }
  }

  protected abstract Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics);
}
