// code by jph & ob
package ch.alpine.sophus.demo.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSlider;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.crv.spline.GeodesicBSplineFunction;
import ch.alpine.sophus.demo.BufferedImageSupplier;
import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.demo.io.PolyDuckietownData;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.math.win.KnotSpacing;
import ch.alpine.sophus.sym.SymLinkImage;
import ch.alpine.sophus.sym.SymLinkImages;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.itp.DeBoor;

/* package */ class DuckietownSmoothingDemo extends AbstractDatasetKernelDemo implements BufferedImageSupplier {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  public static final List<String> LIST = Arrays.asList( //
      "duckie20180713-175124.csv", //
      "duckie20180713-175420.csv", //
      "duckie20180713-175601.csv", //
      "duckie20180901-152902.csv");
  // ---
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerDuckiebot = new SpinnerLabel<>();
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  public DuckietownSmoothingDemo() {
    super(GokartPoseDataV2.INSTANCE);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(2);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(2);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    //
    spinnerDuckiebot.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerDuckiebot.setValue(1);
    spinnerDuckiebot.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "Duckiebot");
    //
    updateState();
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
  }

  @Override
  protected void updateState() {
    PolyDuckietownData polyDuckietownData = PolyDuckietownData.of("/autolab/localization/2018/" + LIST.get(0));
    _control = polyDuckietownData.filter(spinnerDuckiebot.getValue());
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final int degree = spinnerDegree.getValue();
    final int levels = spinnerRefine.getValue();
    final Tensor control = control();
    Tensor effective = control;
    TensorUnaryOperator centripedalKnotSpacing = //
        KnotSpacing.centripetal(manifoldDisplay().parametricDistance(), 0.5);
    Tensor knots = centripedalKnotSpacing.apply(control);
    final Scalar upper = Last.of(knots);
    final Scalar parameter = RationalScalar.of(jSlider.getValue(), jSlider.getMaximum()).multiply(upper);
    // ---
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    GeodesicBSplineFunction scalarTensorFunction = //
        GeodesicBSplineFunction.of(manifoldDisplay.geodesic(), degree, knots, effective);
    RenderQuality.setQuality(graphics);
    Tensor refined = Subdivide.of(RealScalar.ZERO, upper, Math.max(1, control.length() * (1 << levels))).map(scalarTensorFunction);
    {
      Tensor selected = scalarTensorFunction.apply(parameter);
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(manifoldDisplay.shape().multiply(RealScalar.of(0.02)));
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    return refined;
  }

  @Override
  public Scalar markerScale() {
    return RealScalar.of(0.01);
  }

  @Override
  public BufferedImage bufferedImage() {
    final int degree = spinnerDegree.getValue();
    spinnerRefine.getValue();
    final Tensor control = control();
    Tensor effective = control;
    TensorUnaryOperator centripedalKnotSpacing = //
        KnotSpacing.centripetal(manifoldDisplay().parametricDistance(), 0.5);
    Tensor knots = centripedalKnotSpacing.apply(control);
    final Scalar upper = Last.of(knots);
    final Scalar parameter = RationalScalar.of(jSlider.getValue(), jSlider.getMaximum()).multiply(upper);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    GeodesicBSplineFunction scalarTensorFunction = //
        GeodesicBSplineFunction.of(manifoldDisplay.geodesic(), degree, knots, effective);
    DeBoor deBoor = scalarTensorFunction.deBoor(parameter);
    SymLinkImage symLinkImage = SymLinkImages.deboor(deBoor.knots(), deBoor.degree() + 1, parameter);
    return symLinkImage.bufferedImage();
  }

  public static void main(String[] args) {
    new DuckietownSmoothingDemo().setVisible(1000, 800);
  }
}
