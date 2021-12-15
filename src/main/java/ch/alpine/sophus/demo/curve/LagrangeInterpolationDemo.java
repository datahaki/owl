// code by jph
package ch.alpine.sophus.demo.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.stream.IntStream;

import javax.swing.JSlider;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldSelectionArray;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.sophus.crv.spline.LagrangeInterpolation;
import ch.alpine.sophus.demo.Curvature2DRender;
import ch.alpine.sophus.demo.opt.DubinsGenerator;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.R2Display;
import ch.alpine.sophus.sym.SymGeodesic;
import ch.alpine.sophus.sym.SymLinkImage;
import ch.alpine.sophus.sym.SymScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.N;

/** LagrangeInterpolation with extrapolation */
public class LagrangeInterpolationDemo extends AbstractCurvatureDemo {
  @FieldInteger
  @FieldSelectionArray(values = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" })
  public Scalar refine = RealScalar.of(7);
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  public LagrangeInterpolationDemo() {
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    addButtonDubins();
    // ---
    {
      Tensor tensor = Tensors.fromString("{{1, 0, 0}, {1, 0, 2.1}}");
      setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(tensor.stream().map(Times.operator(Tensors.vector(2, 1, 1))))));
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
    if (graph) {
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
    int levels = refine.number().intValue();
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Interpolation interpolation = LagrangeInterpolation.of(manifoldDisplay.geodesic(), getGeodesicControlPoints());
    Tensor refined = Subdivide.of(0, sequence.length(), 1 << levels).map(interpolation::at);
    Tensor render = Tensor.of(refined.stream().map(manifoldDisplay::toPoint));
    Curvature2DRender.of(render, false, geometricLayer, graphics);
    {
      Tensor selected = interpolation.at(parameter);
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(manifoldDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    if (levels < 5)
      renderPoints(manifoldDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    new LagrangeInterpolationDemo().setVisible(1000, 600);
  }
}
