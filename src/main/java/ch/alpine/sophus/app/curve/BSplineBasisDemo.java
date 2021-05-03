// code by jph
package ch.alpine.sophus.app.curve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.crv.spline.GeodesicBSplineFunction;
import ch.alpine.sophus.gds.GeodesicDisplays;
import ch.alpine.sophus.gui.ren.Curvature2DRender;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.lie.rn.RnGeodesic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.itp.BSplineInterpolation;
import ch.alpine.tensor.mat.Inverse;

/* package */ class BSplineBasisDemo extends ControlPointsDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final Color TICKS_COLOR = new Color(0, 0, 0, 128);
  // ---
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleItrp = new JToggleButton("interp");

  BSplineBasisDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    timerFrame.jToolBar.add(jToggleItrp);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(1);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    spinnerRefine.setValue(4);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    int degree = spinnerDegree.getValue();
    int levels = spinnerRefine.getValue();
    Tensor control = getGeodesicControlPoints();
    {
      graphics.setStroke(new BasicStroke(1.25f));
      Tensor matrix = geometricLayer.getMatrix();
      geometricLayer.pushMatrix(Inverse.of(matrix));
      {
        for (int length = 2; length <= 8; ++length) {
          Tensor string = Tensors.fromString("{{100, 0, 0}, {0, -100, 0}, {0, 0, 1}}");
          string.set(RealScalar.of(110 * length), 1, 2);
          geometricLayer.pushMatrix(string);
          for (int k_th = 0; k_th < length; ++k_th) {
            GeodesicBSplineFunction bSplineFunction = //
                GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, UnitVector.of(length, k_th));
            Tensor domain = Subdivide.of(0, length - 1, 100);
            Tensor values = domain.map(bSplineFunction);
            Tensor tensor = Transpose.of(Tensors.of(domain, values));
            graphics.setColor(COLOR_DATA_INDEXED.getColor(k_th));
            graphics.draw(geometricLayer.toPath2D(tensor));
            graphics.setColor(TICKS_COLOR);
            graphics.draw(geometricLayer.toPath2D(Tensors.matrix(new Number[][] { { k_th, 0 }, { k_th, .1 } })));
          }
          geometricLayer.popMatrix();
        }
      }
      geometricLayer.popMatrix();
      graphics.setStroke(new BasicStroke(1f));
    }
    // ---
    Tensor effective = jToggleItrp.isSelected() //
        ? BSplineInterpolation.solve(degree, control)
        : control;
    GeodesicBSplineFunction bSplineFunction = //
        GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, effective);
    Tensor refined = Subdivide.of(0, effective.length() - 1, 4 << levels).map(bSplineFunction);
    renderControlPoints(geometricLayer, graphics);
    Curvature2DRender.of(refined, false, geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new BSplineBasisDemo().setVisible(1000, 800);
  }
}
