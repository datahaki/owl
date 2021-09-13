// code by jph
package ch.alpine.sophus.app.curve;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.app.BufferedImageSupplier;
import ch.alpine.sophus.app.sym.SymLinkImages;
import ch.alpine.sophus.crv.spline.AbstractBSplineInterpolation;
import ch.alpine.sophus.crv.spline.AbstractBSplineInterpolation.Iteration;
import ch.alpine.sophus.crv.spline.GeodesicBSplineFunction;
import ch.alpine.sophus.crv.spline.GeodesicBSplineInterpolation;
import ch.alpine.sophus.crv.spline.LieGroupBSplineInterpolation;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gui.ren.Curvature2DRender;
import ch.alpine.sophus.gui.win.DubinsGenerator;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.sca.Chop;

/* package */ public class GeodesicBSplineFunctionDemo extends AbstractCurveDemo implements BufferedImageSupplier {
  private final JToggleButton jToggleItrp = new JToggleButton("interp");
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

  public GeodesicBSplineFunctionDemo() {
    addButtonDubins();
    // ---
    timerFrame.jToolBar.add(jToggleItrp);
    // ---
    Tensor dubins = Tensors.fromString(
        "{{1, 0, 0}, {1, 0, 0}, {2, 0, 2.5708}, {1, 0, 2.1}, {1.5, 0, 0}, {2.3, 0, -1.2}, {1.5, 0, 0}, {4, 0, 3.14159}, {2, 0, 3.14159}, {2, 0, 0}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
        Tensor.of(dubins.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics, int degree, int levels, Tensor control) {
    final int upper = control.length() - 1;
    final Scalar parameter = sliderRatio().multiply(RealScalar.of(upper));
    bufferedImage = SymLinkImages.symLinkImageGBSF(degree, upper + 1, parameter).bufferedImage();
    // ---
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    // ---
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor effective = control;
    if (jToggleItrp.isSelected()) {
      LieGroup lieGroup = manifoldDisplay.lieGroup();
      AbstractBSplineInterpolation abstractBSplineInterpolation = Objects.isNull(lieGroup) //
          ? new GeodesicBSplineInterpolation(manifoldDisplay.geodesicInterface(), degree, control)
          : new LieGroupBSplineInterpolation(lieGroup, manifoldDisplay.geodesicInterface(), degree, control);
      {
        Tensor tensor = BSplineInterpolationSequence.of(abstractBSplineInterpolation);
        Tensor shape = CirclePoints.of(9).multiply(RealScalar.of(0.05));
        graphics.setColor(new Color(64, 64, 64, 64));
        for (Tensor ctrls : tensor)
          for (Tensor ctrl : ctrls) {
            geometricLayer.pushMatrix(manifoldDisplay.matrixLift(ctrl));
            Path2D path2d = geometricLayer.toPath2D(shape);
            graphics.fill(path2d);
            geometricLayer.popMatrix();
          }
        graphics.setColor(new Color(64, 64, 64, 192));
        for (Tensor ctrls : Transpose.of(tensor))
          graphics.draw(geometricLayer.toPath2D(Tensor.of(ctrls.stream().map(manifoldDisplay::toPoint))));
      }
      Iteration iteration = abstractBSplineInterpolation.untilClose(Chop._06, 100);
      {
        graphics.setColor(Color.BLACK);
        graphics.drawString("" + iteration.steps(), 0, 20);
      }
      effective = iteration.control();
    }
    ScalarTensorFunction scalarTensorFunction = //
        GeodesicBSplineFunction.of(manifoldDisplay.geodesicInterface(), degree, effective);
    {
      Tensor selected = scalarTensorFunction.apply(parameter);
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(manifoldDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    Tensor refined = Subdivide.of(0, upper, Math.max(1, upper * (1 << levels))).map(scalarTensorFunction);
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
    new GeodesicBSplineFunctionDemo().setVisible(1200, 600);
  }
}
