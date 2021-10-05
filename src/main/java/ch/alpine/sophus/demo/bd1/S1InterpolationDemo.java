// code by jph
package ch.alpine.sophus.demo.bd1;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.PathRender;
import ch.alpine.java.ren.PointsRender;
import ch.alpine.sophus.demo.lev.LogWeightingDemo;
import ch.alpine.sophus.demo.opt.LogWeightings;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.Pi;

/* package */ class S1InterpolationDemo extends LogWeightingDemo {
  public S1InterpolationDemo() {
    super(true, ManifoldDisplays.R2_ONLY, LogWeightings.list());
    setMidpointIndicated(false);
    // ---
    setControlPointsSe2(Tensors.fromString("{{1, 0, 0}, {0, 1.2, 0}, {-1, 0, 0}}"));
    timerFrame.geometricComponent.setOffset(500, 500);
    timerFrame.geometricComponent.addRenderInterfaceBackground(S1FrameRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    final Tensor shape = getControlPointShape(); // .multiply(RealScalar.of(0.3));
    if (0 < control.length()) {
      // TODO check for zero norm below
      Tensor sequence = Tensor.of(control.stream().map(Vector2Norm.NORMALIZE));
      Tensor target = sequence;
      graphics.setColor(Color.GREEN);
      for (int index = 0; index < target.length(); ++index)
        graphics.draw(geometricLayer.toLine2D(control.get(index), target.get(index)));
      new PointsRender(new Color(64, 128, 64, 64), new Color(64, 128, 64, 255))
          // new PointsRender(new Color(128, 255, 128, 64), new Color(128, 255, 128, 255)) //
          .show(manifoldDisplay()::matrixLift, shape, target) //
          .render(geometricLayer, graphics);
      // ---
      Tensor values = Tensor.of(control.stream().map(Vector2Norm::of));
      Tensor domain = Subdivide.of(Pi.VALUE.negate(), Pi.VALUE, 511);
      Tensor spherics = domain.map(AngleVector::of);
      // ---
      TensorUnaryOperator tensorUnaryOperator = operator(sequence);
      try {
        ScalarTensorFunction scalarTensorFunction = //
            point -> tensorUnaryOperator.apply(AngleVector.of(point));
        Tensor basis = Tensor.of(domain.stream().parallel().map(Scalar.class::cast).map(scalarTensorFunction));
        Tensor curve = basis.dot(values).pmul(spherics);
        new PathRender(Color.BLUE, 1.25f).setCurve(curve, true).render(geometricLayer, graphics);
        // ---
        Reverse.of(spherics).stream().forEach(curve::append);
        graphics.setColor(new Color(0, 0, 255, 32));
        graphics.fill(geometricLayer.toPath2D(curve));
      } catch (Exception exception) {
        // ---
      }
    }
    renderControlPoints(geometricLayer, graphics);
    // POINTS_RENDER_0.show(geodesicDisplay()::matrixLift, shape, getGeodesicControlPoints()).render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new S1InterpolationDemo().setVisible(1000, 800);
  }
}
