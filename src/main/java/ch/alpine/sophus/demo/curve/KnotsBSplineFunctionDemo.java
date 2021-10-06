// code by jph
package ch.alpine.sophus.demo.curve;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.gui.ToolbarFieldsEditor;
import ch.alpine.sophus.crv.spline.GeodesicBSplineFunction;
import ch.alpine.sophus.demo.BufferedImageSupplier;
import ch.alpine.sophus.demo.Curvature2DRender;
import ch.alpine.sophus.demo.opt.DubinsGenerator;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.math.win.KnotSpacing;
import ch.alpine.sophus.sym.SymLinkImage;
import ch.alpine.sophus.sym.SymLinkImages;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.itp.DeBoor;

public class KnotsBSplineFunctionDemo extends AbstractCurveDemo implements BufferedImageSupplier {
  @FieldSlider
  @FieldPreferredWidth(width = 200)
  @FieldClip(min = "0", max = "1")
  public Scalar exponent = RealScalar.ONE;
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

  public KnotsBSplineFunctionDemo() {
    super(ManifoldDisplays.CL_SE2_R2);
    // ---
    refine = RealScalar.of(5);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    Tensor dubins = Tensors.fromString(
        "{{1, 0, 0}, {1, 0, 0}, {2, 0, 2.5708}, {1, 0, 2.1}, {1.5, 0, 0}, {2.3, 0, -1.2}, {1.5, 0, 0}, {4, 0, 3.14159}, {2, 0, 3.14159}, {2, 0, 0}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
        Tensor.of(dubins.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics, int degree, int levels, Tensor control) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor knots = KnotSpacing.centripetal(manifoldDisplay.parametricDistance(), exponent).apply(control);
    Scalar upper = Last.of(knots);
    Scalar parameter = ratio.multiply(upper);
    // ---
    GeodesicBSplineFunction scalarTensorFunction = //
        GeodesicBSplineFunction.of(manifoldDisplay.geodesic(), degree, knots, control);
    {
      DeBoor deBoor = scalarTensorFunction.deBoor(parameter);
      SymLinkImage symLinkImage = SymLinkImages.deboor(deBoor.knots(), deBoor.degree() + 1, parameter);
      bufferedImage = symLinkImage.bufferedImage();
    }
    // ---
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    Tensor refined = Subdivide.of(RealScalar.ZERO, upper, Math.max(1, control.length() * (1 << levels))).map(scalarTensorFunction);
    {
      Tensor selected = scalarTensorFunction.apply(parameter);
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(manifoldDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    Tensor render = Tensor.of(refined.stream().map(manifoldDisplay::toPoint));
    Curvature2DRender.of(render, false, geometricLayer, graphics);
    if (levels < 5)
      renderPoints(manifoldDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public static void main(String[] args) {
    new KnotsBSplineFunctionDemo().setVisible(1200, 600);
  }
}
