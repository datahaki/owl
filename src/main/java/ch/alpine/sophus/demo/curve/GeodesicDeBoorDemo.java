// code by jph
package ch.alpine.sophus.demo.curve;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.crv.spline.GeodesicBSplineFunction;
import ch.alpine.sophus.demo.BufferedImageSupplier;
import ch.alpine.sophus.demo.Curvature2DRender;
import ch.alpine.sophus.demo.opt.DubinsGenerator;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.Se2CoveringDisplay;
import ch.alpine.sophus.sym.SymLinkImages;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.itp.DeBoor;
import ch.alpine.tensor.red.Times;

// TODO OWL ALG demo does not seem correct
public class GeodesicDeBoorDemo extends AbstractCurveDemo implements BufferedImageSupplier {
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

  public GeodesicDeBoorDemo() {
    addButtonDubins();
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    setGeodesicDisplay(Se2CoveringDisplay.INSTANCE);
    // ---
    Tensor dubins = Tensors.fromString("{{1, 0, 0}, {2, 0, 2.5708}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 0), //
        Tensor.of(dubins.stream().map(Times.operator(Tensors.vector(2, 1, 1))))));
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics, int degree, int levels, Tensor control) {
    final int upper = control.length() - 1;
    final Scalar parameter = ratio.multiply(RealScalar.of(upper));
    Tensor knots = Range.of(0, 2 * upper);
    bufferedImage = SymLinkImages.deboor(knots, control.length(), parameter).bufferedImage();
    // ---
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    // ---
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Geodesic geodesicInterface = manifoldDisplay.geodesic();
    ScalarTensorFunction scalarTensorFunction = //
        DeBoor.of(geodesicInterface, knots, control);
    GeodesicBSplineFunction.of(manifoldDisplay.geodesic(), degree, control);
    Scalar center = RationalScalar.of(control.length() - 1, 2);
    Tensor refined = Subdivide.of( //
        center.subtract(RationalScalar.HALF), //
        center.add(RationalScalar.HALF), //
        Math.max(1, upper * (1 << levels))).map(scalarTensorFunction);
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

  @Override
  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public static void main(String[] args) {
    new GeodesicDeBoorDemo().setVisible(1200, 600);
  }
}
