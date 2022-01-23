// code by jph
package ch.alpine.sophus.demo.curve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldSelectionArray;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.win.LookAndFeels;
import ch.alpine.sophus.crv.spline.GeodesicBSplineFunction;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.demo.Curvature2DRender;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.lie.rn.RnGeodesic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.itp.BSplineInterpolation;
import ch.alpine.tensor.mat.re.Inverse;

public class BSplineBasisDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final Color TICKS_COLOR = new Color(0, 0, 0, 128);
  // ---
  @FieldInteger
  @FieldSelectionArray(value = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" })
  public Scalar degree = RealScalar.of(1);
  @FieldInteger
  @FieldSelectionArray(value = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" })
  public Scalar refine = RealScalar.of(4);
  public Boolean interp = false;

  public BSplineBasisDemo() {
    super(true, ManifoldDisplays.R2_ONLY);
    // ---
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    int _degree = degree.number().intValue();
    int _levels = refine.number().intValue();
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
                GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, _degree, UnitVector.of(length, k_th));
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
    Tensor effective = interp //
        ? BSplineInterpolation.solve(_degree, control)
        : control;
    GeodesicBSplineFunction bSplineFunction = //
        GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, _degree, effective);
    Tensor refined = Subdivide.of(0, effective.length() - 1, 4 << _levels).map(bSplineFunction);
    renderControlPoints(geometricLayer, graphics);
    Curvature2DRender.of(refined, false, geometricLayer, graphics);
  }

  public static void main(String[] args) {
    LookAndFeels.INTELLI_J.updateUI();
    new BSplineBasisDemo().setVisible(1000, 800);
  }
}
