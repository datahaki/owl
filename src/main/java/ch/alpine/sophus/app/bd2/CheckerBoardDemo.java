// code by jph
package ch.alpine.sophus.app.bd2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.awt.SpinnerListener;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.gui.region.ImageRender;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.app.lev.LeversRender;
import ch.alpine.sophus.app.lev.LogWeightingBase;
import ch.alpine.sophus.gds.GeodesicArrayPlot;
import ch.alpine.sophus.gds.H2Display;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.R2Display;
import ch.alpine.sophus.gds.S2Display;
import ch.alpine.sophus.gui.ren.ArrayPlotRender;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.sophus.opt.LogWeighting;
import ch.alpine.sophus.opt.PolygonCoordinates;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class CheckerBoardDemo extends LogWeightingBase //
    implements SpinnerListener<ManifoldDisplay> {
  public static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._000.strict();
  public static final Tensor BOX = Tensors.fromString("{{1, 1}, {-1, 1}, {-1, -1}, {1, -1}}");
  // ---
  final SpinnerLabel<ParameterizationPattern> spinnerPattern = SpinnerLabel.of(ParameterizationPattern.values());
  final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  final SpinnerLabel<Integer> spinnerFactor = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("freeze");
  private Tensor reference;

  public CheckerBoardDemo() {
    super(true, ManifoldDisplays.R2_H2_S2, PolygonCoordinates.list());
    spinnerLogWeighting.addSpinnerListener(v -> recompute());
    {
      spinnerPattern.addSpinnerListener(v -> recompute());
      spinnerPattern.addToComponentReduced(timerFrame.jToolBar, new Dimension(160, 28), "pattern");
    }
    {
      spinnerFactor.setList(Arrays.asList(2, 3, 5, 8, 10, 15, 20));
      spinnerFactor.setValue(5);
      spinnerFactor.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
      spinnerFactor.addSpinnerListener(v -> recompute());
    }
    {
      spinnerRefine.setList(Arrays.asList(50, 80, 120, 160, 240, 360));
      spinnerRefine.setValue(50);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
      spinnerRefine.addSpinnerListener(v -> recompute());
    }
    // ---
    timerFrame.jToolBar.add(jToggleButton);
    // ---
    ManifoldDisplay manifoldDisplay = R2Display.INSTANCE;
    actionPerformed(manifoldDisplay);
    addSpinnerListener(this);
    addSpinnerListener(l -> recompute());
    recompute();
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    // ---
    addMouseRecomputation();
  }

  public void actionPerformed(ActionEvent actionEvent) {
    System.out.println("export");
    if (jToggleButton.isSelected()) {
      Tensor sequence = getGeodesicControlPoints();
      // LeversRender leversRender = LeversRender.of( //
      // geodesicDisplay(), getGeodesicControlPoints(), null, geometricLayer, graphics);
      // leversRender.renderSurfaceP();
      File folder = HomeDirectory.Pictures(CheckerBoardDemo.class.getSimpleName());
      folder.mkdir();
      for (LogWeighting logWeighting : PolygonCoordinates.list())
        try {
          System.out.println(logWeighting);
          TensorScalarFunction tensorUnaryOperator = function(sequence, reference.multiply(DoubleScalar.of(spinnerFactor.getValue())));
          GeodesicArrayPlot geodesicArrayPlot = manifoldDisplay().geodesicArrayPlot();
          // LONGTERM redundant
          Tensor matrix = geodesicArrayPlot.raster(512, tensorUnaryOperator, DoubleScalar.INDETERMINATE);
          BufferedImage bufferedImage = ArrayPlotRender.rescale(matrix, COLOR_DATA_INDEXED, 1).export();
          ImageIO.write(bufferedImage, "png", new File(folder, logWeighting.toString() + ".png"));
          // RenderQuality.setDefault(graphics); // default so that raster becomes visible
          // Tensor pixel2model = geodesicArrayPlot.pixel2model(new Dimension(bufferedImage.getHeight(), bufferedImage.getHeight()));
          // ImageRender.of(bufferedImage, pixel2model).render(geometricLayer, graphics);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  }

  private BufferedImage bufferedImage;

  @Override
  protected void recompute() {
    if (jToggleButton.isSelected()) {
      System.out.println("compute");
      Tensor sequence = getGeodesicControlPoints();
      GeodesicArrayPlot geodesicArrayPlot = manifoldDisplay().geodesicArrayPlot();
      Tensor matrix = geodesicArrayPlot.raster( //
          spinnerRefine.getValue(), //
          function(sequence, reference.multiply(DoubleScalar.of(spinnerFactor.getValue()))), //
          DoubleScalar.INDETERMINATE);
      bufferedImage = ArrayPlotRender.rescale(matrix, COLOR_DATA_INDEXED, 1).export();
    } else {
      bufferedImage = null;
    }
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.draw(geometricLayer.toPath2D(BOX, true));
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    if (jToggleButton.isSelected()) {
      LeversRender leversRender = LeversRender.of( //
          manifoldDisplay(), getGeodesicControlPoints(), null, geometricLayer, graphics);
      leversRender.renderSurfaceP();
      if (Objects.isNull(bufferedImage))
        recompute();
      if (Objects.nonNull(bufferedImage)) {
        RenderQuality.setDefault(graphics); // default so that raster becomes visible
        GeodesicArrayPlot geodesicArrayPlot = manifoldDisplay().geodesicArrayPlot();
        Tensor pixel2model = geodesicArrayPlot.pixel2model(new Dimension(bufferedImage.getHeight(), bufferedImage.getHeight()));
        ImageRender.of(bufferedImage, pixel2model).render(geometricLayer, graphics);
      }
    } else {
      reference = getGeodesicControlPoints();
      LeversRender leversRender = LeversRender.of( //
          manifoldDisplay(), reference, null, geometricLayer, graphics);
      leversRender.renderSurfaceP();
      bufferedImage = null;
    }
  }

  int resolution() {
    return 120; // for sequence of length 6
  }

  @Override
  public void actionPerformed(ManifoldDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof R2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.287, -0.958, 0.000}, {-1.017, -0.953, 0.000}, {-0.717, 0.229, 0.000}, {-0.912, 0.669, 0.000}, {-0.644, 0.967, 0.000}, {0.933, 0.908, 0.000}, {0.950, -0.209, 0.000}, {-0.461, 0.637, 0.000}, {0.956, -0.627, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof H2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.783, -2.467, 0.000}, {-0.083, -1.667, 0.000}, {-2.683, -1.167, 0.000}, {-2.650, 0.133, 0.000}, {-1.450, 2.467, 0.000}, {0.083, 0.033, 0.000}, {0.867, 2.383, 0.000}, {2.217, 2.500, 0.000}, {2.183, -0.517, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof S2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.715, -0.357, 0.000}, {-0.708, 0.500, 0.000}, {-0.102, 0.592, 0.000}, {0.181, 0.892, 0.000}, {0.733, 0.455, 0.000}, {-0.349, 0.232, 0.000}, {-0.226, 0.008, 0.000}, {0.434, 0.097, 0.000}, {0.759, -0.492, 0.000}, {0.067, -0.712, 0.000}}"));
    }
  }

  @Override
  protected TensorUnaryOperator operator(VectorLogManifold vectorLogManifold, Tensor sequence) {
    // biinvariant and variogram are not necessary
    return logWeighting().operator(null, vectorLogManifold, null, sequence);
  }

  @Override
  protected TensorScalarFunction function(Tensor sequence, Tensor values) {
    TensorUnaryOperator operator = operator(sequence);
    TensorUnaryOperator dot_prod = point -> operator.apply(point).dot(values);
    return spinnerPattern.getValue().apply(dot_prod);
  }

  public static void main(String[] args) {
    new CheckerBoardDemo().setVisible(1300, 900);
  }
}
