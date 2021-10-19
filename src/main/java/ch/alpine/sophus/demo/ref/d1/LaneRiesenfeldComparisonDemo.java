// code by gjoel
package ch.alpine.sophus.demo.ref.d1;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualRow;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.PathRender;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.demo.CurveVisualSet;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.math.MinMax;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Differences;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Quantile;

/** compare different levels of smoothing in the LaneRiesenfeldCurveSubdivision */
/* package */ class LaneRiesenfeldComparisonDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLORS = ColorDataLists._097.cyclic();
  private static final List<CurveSubdivisionSchemes> CURVE_SUBDIVISION_SCHEMES = //
      CurveSubdivisionHelper.LANE_RIESENFELD;
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleCurvature = new JToggleButton("crvt");
  private final List<PathRender> pathRenders = new ArrayList<>();

  public LaneRiesenfeldComparisonDemo() {
    this(ManifoldDisplays.WITHOUT_Sn_SO3);
  }

  public LaneRiesenfeldComparisonDemo(List<ManifoldDisplay> list) {
    super(true, list);
    // ---
    jToggleCurvature.setSelected(true);
    jToggleCurvature.setToolTipText("curvature plot");
    timerFrame.jToolBar.add(jToggleCurvature);
    // ---
    Tensor control = Tensors.fromString("{{0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {3, 1, 0}, {4, 1, 0}, {5, 0, 0}, {6, 0, 0}, {7, 0, 0}}").multiply(RealScalar.of(2));
    setControlPointsSe2(control);
    timerFrame.jToolBar.addSeparator();
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(4);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    for (int i = 0; i < CURVE_SUBDIVISION_SCHEMES.size(); ++i)
      pathRenders.add(new PathRender(COLORS.getColor(i)));
    // ---
    timerFrame.geometricComponent.setOffset(100, 600);
  }

  @Override // from RenderInterface
  public synchronized final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    VisualSet visualSet1 = new VisualSet();
    visualSet1.setPlotLabel("Curvature");
    visualSet1.setAxesLabelX("length");
    visualSet1.setAxesLabelY("curvature");
    // ---
    VisualSet visualSet2 = new VisualSet();
    visualSet2.setPlotLabel("Curvature d/ds");
    visualSet2.setAxesLabelX("length");
    visualSet2.setAxesLabelY("curvature d/ds");
    for (int i = 0; i < CURVE_SUBDIVISION_SCHEMES.size(); ++i) {
      Tensor refined = curve(geometricLayer, graphics, i);
      if (jToggleCurvature.isSelected() && 1 < refined.length()) {
        Tensor tensor = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
        VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
        CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
        VisualRow visualRow = curveVisualSet.addCurvature(visualSet);
        Tensor curvature = visualRow.points();
        // ---
        Tensor curvatureRy = Tensor.of(Differences.of(curvature).stream().map(t -> t.Get(1).divide(t.Get(0))));
        Tensor curvatureRx = Tensor.of(IntStream.range(1, curvature.length()).mapToObj(j -> {
          Tensor domain = curvature.get(Tensor.ALL, 0);
          return Mean.of(domain.extract(j - 1, j + 1));
        }));
        // ---
        VisualRow visualRow1 = visualSet1.add(curvature);
        visualRow1.setLabel(CURVE_SUBDIVISION_SCHEMES.get(i).name());
        visualRow1.setColor(COLORS.getColor(i));
        // ---
        VisualRow visualRow2 = visualSet2.add(curvatureRx, curvatureRy);
        visualRow2.setLabel(CURVE_SUBDIVISION_SCHEMES.get(i).name());
        visualRow2.setColor(COLORS.getColor(i));
      }
    }
    // ---
    Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    if (jToggleCurvature.isSelected()) {
      JFreeChart jFreeChart1 = ListPlot.of(visualSet1, true);
      jFreeChart1.draw(graphics, new Rectangle2D.Double(dimension.width * .5, 0, dimension.width * .5, dimension.height * .5));
      // ---
      JFreeChart jFreeChart2 = ListPlot.of(visualSet2, true);
      if (!visualSet2.visualRows().isEmpty()) {
        Tensor tensorMin = Tensor.of(visualSet2.visualRows().stream() //
            .map(VisualRow::points) //
            .map(points -> points.get(Tensor.ALL, 1)) //
            .map(MinMax::of) //
            .map(MinMax::min));
        double min = Quantile.of(tensorMin).apply(RationalScalar.of(1, CURVE_SUBDIVISION_SCHEMES.size() - 1)).number().doubleValue();
        Tensor tensorMax = Tensor.of(visualSet2.visualRows().stream() //
            .map(VisualRow::points) //
            .map(points -> points.get(Tensor.ALL, 1)) //
            .map(MinMax::of) //
            .map(MinMax::max));
        double max = Quantile.of(tensorMax) //
            .apply(RationalScalar.of(CURVE_SUBDIVISION_SCHEMES.size() - 1, CurveSubdivisionHelper.LANE_RIESENFELD.size() - 1)).number().doubleValue();
        if (min != max)
          jFreeChart2.getXYPlot().getRangeAxis().setRange(1.1 * min, 1.1 * max);
      }
      jFreeChart2.draw(graphics, new Rectangle2D.Double(dimension.width * .5, dimension.height * .5, dimension.width * .5, dimension.height * .5));
    }
    RenderQuality.setDefault(graphics);
  }

  public Tensor curve(GeometricLayer geometricLayer, Graphics2D graphics, int index) {
    CurveSubdivisionSchemes scheme = CURVE_SUBDIVISION_SCHEMES.get(index);
    PathRender pathRender = pathRenders.get(index);
    // ---
    Tensor control = getGeodesicControlPoints();
    int levels = spinnerRefine.getValue();
    renderControlPoints(geometricLayer, graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Geodesic geodesicInterface = geodesicDisplay.geodesic();
    Tensor refined = StaticHelper.refine(control, levels, scheme.of(geodesicDisplay), //
        CurveSubdivisionHelper.isDual(scheme), false, geodesicInterface);
    // ---
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    pathRender.setCurve(render, false);
    pathRender.render(geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    new LaneRiesenfeldComparisonDemo().setVisible(1200, 800);
  }
}
