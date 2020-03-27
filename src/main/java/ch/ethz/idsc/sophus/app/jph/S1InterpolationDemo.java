// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.function.Supplier;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.app.api.SnBarycentricCoordinates;
import ch.ethz.idsc.sophus.lie.so2.AngleVector;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.win.WeightingInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class S1InterpolationDemo extends ControlPointsDemo {
  private final SpinnerLabel<Supplier<WeightingInterface>> spinnerBarycentric = new SpinnerLabel<>();
  private static final Tensor CIRCLE = CirclePoints.of(61).map(N.DOUBLE);
  private final RenderInterface renderInterface = new RenderInterface() {
    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      GraphicsUtil.setQualityDefault(graphics);
      Tensor box = Tensors.fromString("{{-1, -1}, {1, -1}, {1, 1}, {-1, 1}}").multiply(RealScalar.of(2.5));
      Path2D path2d = geometricLayer.toPath2D(box, true);
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(path2d);
      GraphicsUtil.setQualityHigh(graphics);
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(CIRCLE, true));
    }
  };

  public S1InterpolationDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    setMidpointIndicated(false);
    {
      spinnerBarycentric.setArray(SnBarycentricCoordinates.values());
      spinnerBarycentric.setIndex(0);
      spinnerBarycentric.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "barycentric");
    }
    // ---
    setControlPointsSe2(Tensors.fromString("{{1, 0, 0}, {0, 1.2, 0}, {-1, 0, 0}}"));
    // Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    // timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(2, 2, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(500, 500);
    timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    Tensor control = getGeodesicControlPoints();
    final Tensor shape = getControlPointShape(); // .multiply(RealScalar.of(0.3));
    if (1 < control.length()) {
      // TODO check for zero norm below
      Tensor sequence = Tensor.of(control.stream().map(Normalize.with(Norm._2)));
      Tensor target = sequence;
      graphics.setColor(Color.GREEN);
      for (int index = 0; index < target.length(); ++index)
        graphics.draw(geometricLayer.toLine2D(control.get(index), target.get(index)));
      new PointsRender(new Color(64, 128, 64, 64), new Color(64, 128, 64, 255))
          // new PointsRender(new Color(128, 255, 128, 64), new Color(128, 255, 128, 255)) //
          .show(geodesicDisplay()::matrixLift, shape, target) //
          .render(geometricLayer, graphics);
      // ---
      Tensor values = Tensor.of(control.stream().map(Norm._2::ofVector));
      Tensor domain = Subdivide.of(Pi.VALUE.negate(), Pi.VALUE, 511);
      Tensor spherics = domain.map(AngleVector::of);
      // ---
      WeightingInterface weightingInterface = spinnerBarycentric.getValue().get();
      try {
        ScalarTensorFunction scalarTensorFunction = //
            point -> weightingInterface.weights(sequence, AngleVector.of(point));
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
