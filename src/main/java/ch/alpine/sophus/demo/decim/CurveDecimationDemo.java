// code by jph
package ch.alpine.sophus.demo.decim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldSelectionArray;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.ren.PathRender;
import ch.alpine.sophus.decim.CurveDecimation;
import ch.alpine.sophus.decim.DecimationResult;
import ch.alpine.sophus.decim.LineDistances;
import ch.alpine.sophus.demo.io.GokartPoseData;
import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.demo.io.GokartPoseDatas;
import ch.alpine.sophus.flt.CenterFilter;
import ch.alpine.sophus.flt.ga.GeodesicCenter;
import ch.alpine.sophus.gds.GeodesicDatasetDemo;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.lie.se2.Se2Geodesic;
import ch.alpine.sophus.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.sca.win.WindowFunctions;

/* package */ class CurveDecimationDemo extends GeodesicDatasetDemo {
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_SHAPE = new Color(160, 160, 160, 160);
  private static final Color COLOR_RECON = new Color(128, 128, 128, 255);
  private static final int WIDTH = 480;
  private static final int HEIGHT = 360;
  // ---
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_RECON, 2f);

  // ---
  public static class Param {
    @FieldSelectionArray(value = { "0", "1", "5", "8", "10", "15", "20", "25", "30", "35" })
    public Scalar width = RealScalar.of(0);
    @FieldSelectionArray(value = { "0", "1", "2", "3", "4", "5" })
    public Scalar level = RealScalar.of(2);
    @FieldSelectionArray(value = { "1", "2", "3" })
    public Scalar degre = RealScalar.of(1);
    public LineDistances type = LineDistances.STANDARD;
    public Boolean error = false;
  }

  // private final SpinnerLabel<Integer> spinnerLabelWidth = new SpinnerLabel<>();
  // private final SpinnerLabel<Integer> spinnerLabelLevel = new SpinnerLabel<>();
  // private final SpinnerLabel<Integer> spinnerLabelDegre = new SpinnerLabel<>();
  // private final SpinnerLabel<LineDistances> spinnerType = new SpinnerLabel<>();
  // // private final JSlider jSlider = new JSlider(1, 1000, 500);
  // private final JToggleButton jToggleButton = new JToggleButton("error");
  protected Tensor _control = Tensors.empty();
  private final Param param = new Param();

  public CurveDecimationDemo(GokartPoseData gokartPoseData) {
    super(ManifoldDisplays.SE2_R2, gokartPoseData);
    ToolbarFieldsEditor.add(param, timerFrame.jToolBar).addUniversalListener(this::updateState);
    // ---
    timerFrame.geometricComponent.setModel2Pixel(GokartPoseDatas.HANGAR_MODEL2PIXEL);
    updateState();
  }

  @Override
  protected void updateState() {
    int limit = spinnerLabelLimit.getValue();
    String name = spinnerLabelString.getValue();
    TensorUnaryOperator tensorUnaryOperator = new CenterFilter( //
        GeodesicCenter.of(Se2Geodesic.INSTANCE, WindowFunctions.GAUSSIAN.get()), param.width.number().intValue());
    _control = tensorUnaryOperator.apply(gokartPoseData.getPose(name, limit));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    {
      final Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(0.3));
      pathRenderCurve.setCurve(_control, false).render(geometricLayer, graphics);
      if (_control.length() <= 1000)
        for (Tensor point : _control) {
          geometricLayer.pushMatrix(manifoldDisplay.matrixLift(point));
          Path2D path2d = geometricLayer.toPath2D(shape);
          path2d.closePath();
          graphics.setColor(new Color(255, 128, 128, 64));
          graphics.fill(path2d);
          graphics.setColor(COLOR_CURVE);
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
    }
    Scalar epsilon = Power.of(RationalScalar.HALF, param.level.number().intValue());
    // epsilon = RationalScalar.of(jSlider.getValue(), jSlider.getMaximum() * 3);
    CurveDecimation curveDecimation = CurveDecimation.of( //
        param.type.supply(manifoldDisplay.hsManifold()), epsilon);
    Tensor control = Tensor.of(_control.stream().map(manifoldDisplay::project));
    DecimationResult result = curveDecimation.evaluate(control);
    Tensor simplified = result.result();
    graphics.setColor(Color.DARK_GRAY);
    // graphics.drawString("SIMPL=" + control.length(), 0, 20);
    // graphics.drawString("SIMPL=" + , 0, 30);
    Tensor refined = Nest.of( //
        LaneRiesenfeldCurveSubdivision.of(manifoldDisplay.geodesic(), param.degre.number().intValue())::string, //
        simplified, 5);
    pathRenderShape.setCurve(refined, false).render(geometricLayer, graphics);
    {
      final Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(0.8));
      for (Tensor point : simplified) {
        geometricLayer.pushMatrix(manifoldDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(COLOR_SHAPE);
        graphics.fill(path2d);
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    if (param.error) {
      Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
      VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
      visualSet.setPlotLabel("Reduction from " + control.length() + " to " + simplified.length() + " samples");
      visualSet.getAxisX().setLabel("sample no.");
      visualSet.getAxisY().setLabel("error");
      // visualSet.setPlotLabel("error");
      visualSet.add(Range.of(0, control.length()), result.errors());
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    }
  }

  public static void main(String[] args) {
    new CurveDecimationDemo(GokartPoseDataV2.RACING_DAY).setVisible(1000, 800);
  }
}
