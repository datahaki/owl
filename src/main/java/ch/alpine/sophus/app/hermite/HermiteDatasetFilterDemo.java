// code by jph
package ch.alpine.sophus.app.hermite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.app.io.GokartPoseDataV2;
import ch.alpine.sophus.app.io.GokartPoseDatas;
import ch.alpine.sophus.gds.GeodesicDatasetDemo;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.lie.se2.Se2BiinvariantMeans;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.lie.se2c.Se2CoveringExponential;
import ch.alpine.sophus.math.Do;
import ch.alpine.sophus.math.TensorIteration;
import ch.alpine.sophus.ref.d1h.Hermite3Filter;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.sca.Power;

/* package */ class HermiteDatasetFilterDemo extends GeodesicDatasetDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_RECON = new Color(128, 128, 128, 255);
  // ---
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_RECON, 2f);
  // ---
  private final GokartPoseDataV2 gokartPoseData;
  private final SpinnerLabel<Integer> spinnerLabelSkips = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelLevel = new SpinnerLabel<>();
  private final JToggleButton jToggleAdjoint = new JToggleButton("ad");
  private final JToggleButton jToggleButton = new JToggleButton("derivatives");
  protected Tensor _control = Tensors.empty();

  public HermiteDatasetFilterDemo(GokartPoseDataV2 gokartPoseData) {
    super(ManifoldDisplays.SE2_ONLY, gokartPoseData);
    this.gokartPoseData = gokartPoseData;
    timerFrame.geometricComponent.setModel2Pixel(GokartPoseDatas.HANGAR_MODEL2PIXEL);
    {
      spinnerLabelSkips.setList(Arrays.asList(1, 2, 5, 10, 25, 50));
      spinnerLabelSkips.setValue(5);
      spinnerLabelSkips.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "skips");
      spinnerLabelSkips.addSpinnerListener(type -> updateState());
    }
    timerFrame.jToolBar.addSeparator();
    {
      spinnerLabelLevel.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
      spinnerLabelLevel.setValue(1);
      spinnerLabelLevel.addToComponentReduced(timerFrame.jToolBar, new Dimension(40, 28), "level");
    }
    timerFrame.jToolBar.addSeparator();
    {
      timerFrame.jToolBar.add(jToggleAdjoint);
    }
    {
      jToggleButton.setSelected(true);
      jToggleButton.setToolTipText("show derivatives");
      timerFrame.jToolBar.add(jToggleButton);
    }
    updateState();
  }

  @Override
  protected void updateState() {
    int limit = spinnerLabelLimit.getValue();
    String name = spinnerLabelString.getValue();
    Tensor control = gokartPoseData.getPoseVel(name, limit);
    Tensor result = Tensors.empty();
    int skips = spinnerLabelSkips.getValue();
    for (int index = 0; index < control.length(); index += skips)
      result.append(control.get(index));
    _control = result;
  }

  @SuppressWarnings("unused")
  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    {
      final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(0.3));
      pathRenderCurve.setCurve(_control.get(Tensor.ALL, 0), false).render(geometricLayer, graphics);
      if (_control.length() <= 1000)
        for (Tensor point : _control.get(Tensor.ALL, 0)) {
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
          Path2D path2d = geometricLayer.toPath2D(shape);
          path2d.closePath();
          graphics.setColor(new Color(255, 128, 128, 64));
          graphics.fill(path2d);
          graphics.setColor(COLOR_CURVE);
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
    }
    graphics.setColor(Color.DARK_GRAY);
    Scalar delta = RationalScalar.of(spinnerLabelSkips.getValue(), 50);
    TensorIteration tensorIteration = //
        // new Hermite1Filter(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE).string(delta, _control);
        new Hermite3Filter(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE, Se2BiinvariantMeans.FILTER) //
            .string(delta, _control);
    int levels = 2 * spinnerLabelLevel.getValue();
    Tensor refined = Do.of(_control, tensorIteration::iterate, levels);
    {
      final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(0.3));
      for (Tensor point : refined.get(Tensor.ALL, 0)) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(new Color(128, 255, 128, 64));
        graphics.fill(path2d);
        graphics.setColor(COLOR_CURVE);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    pathRenderShape.setCurve(refined.get(Tensor.ALL, 0), false).render(geometricLayer, graphics);
    if (jToggleButton.isSelected()) {
      Tensor deltas = refined.get(Tensor.ALL, 1);
      int dims = deltas.get(0).length();
      if (0 < deltas.length()) {
        JFreeChart jFreeChart = StaticHelper.listPlot(deltas, //
            Range.of(0, deltas.length()).multiply(delta).divide(Power.of(2, levels)));
        Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
        jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
      }
    }
  }

  public static void main(String[] args) {
    new HermiteDatasetFilterDemo(GokartPoseDataV2.RACING_DAY).setVisible(1000, 800);
  }
}
