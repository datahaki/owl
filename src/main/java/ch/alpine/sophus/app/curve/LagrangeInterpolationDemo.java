// code by jph
package ch.alpine.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.app.sym.SymGeodesic;
import ch.alpine.sophus.app.sym.SymLinkImage;
import ch.alpine.sophus.app.sym.SymScalar;
import ch.alpine.sophus.crv.spline.LagrangeInterpolation;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.R2Display;
import ch.alpine.sophus.gui.ren.Curvature2DRender;
import ch.alpine.sophus.gui.win.DubinsGenerator;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.sca.N;

/** LagrangeInterpolation with extrapolation */
/* package */ class LagrangeInterpolationDemo extends AbstractCurvatureDemo {
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleSymi = new JToggleButton("graph");
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  public LagrangeInterpolationDemo() {
    addButtonDubins();
    // ---
    jToggleSymi.setSelected(true);
    timerFrame.jToolBar.add(jToggleSymi);
    // ---
    spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    spinnerRefine.setValue(7);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    {
      Tensor tensor = Tensors.fromString("{{1, 0, 0}, {1, 0, 2.1}}");
      setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(tensor.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    setGeodesicDisplay(R2Display.INSTANCE);
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor sequence = getGeodesicControlPoints();
    if (Tensors.isEmpty(sequence))
      return Tensors.empty();
    final Scalar parameter = RationalScalar.of(jSlider.getValue(), jSlider.getMaximum()) //
        .multiply(RealScalar.of(sequence.length()));
    if (jToggleSymi.isSelected()) {
      Tensor vector = Tensor.of(IntStream.range(0, sequence.length()).mapToObj(SymScalar::leaf));
      ScalarTensorFunction scalarTensorFunction = LagrangeInterpolation.of(SymGeodesic.INSTANCE, vector)::at;
      Scalar scalar = N.DOUBLE.apply(parameter);
      SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(scalar);
      graphics.drawImage(new SymLinkImage(symScalar).bufferedImage(), 0, 0, null);
    }
    // ---
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    int levels = spinnerRefine.getValue();
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Interpolation interpolation = LagrangeInterpolation.of(geodesicDisplay.geodesicInterface(), getGeodesicControlPoints());
    Tensor refined = Subdivide.of(0, sequence.length(), 1 << levels).map(interpolation::at);
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    Curvature2DRender.of(render, false, geometricLayer, graphics);
    {
      Tensor selected = interpolation.at(parameter);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    if (levels < 5)
      renderPoints(geodesicDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    new LagrangeInterpolationDemo().setVisible(1000, 600);
  }
}
