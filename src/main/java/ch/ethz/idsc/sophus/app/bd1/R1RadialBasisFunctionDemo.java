// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.krg.RadialBasisFunctionInterpolation;
import ch.ethz.idsc.sophus.krg.ShepardInterpolation;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class R1RadialBasisFunctionDemo extends B1KrigingDemo {
  public R1RadialBasisFunctionDemo() {
    super(R2GeodesicDisplay.INSTANCE);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    Tensor control = Sort.of(getControlPointsSe2());
    if (1 < control.length()) {
      Tensor support = control.get(Tensor.ALL, 0);
      Tensor funceva = control.get(Tensor.ALL, 1);
      // ---
      Tensor sequence = support.map(Tensors::of);
      ScalarUnaryOperator variogram = variogram();
      WeightingInterface weightingInterface = spinnerDistances.getValue().of(geodesicDisplay().flattenLogManifold(), variogram);
      Tensor domain = domain();
      try {
        TensorUnaryOperator tensorUnaryOperator = //
            RadialBasisFunctionInterpolation.normalized(weightingInterface, sequence, funceva);
        Tensor result = Tensor.of(domain.stream().map(Tensors::of).map(tensorUnaryOperator));
        new PathRender(Color.BLUE, 1.25f) //
            .setCurve(Transpose.of(Tensors.of(domain, result)), false) //
            .render(geometricLayer, graphics);
      } catch (Exception exception) {
        // ---
      }
      if (!isDeterminate())
        try {
          WeightingInterface shepardInterpolation = ShepardInterpolation.of(weightingInterface, funceva);
          Tensor result = Tensor.of(domain.stream().map(Tensors::of).map(point -> shepardInterpolation.weights(sequence, point)));
          new PathRender(Color.RED, 1.25f) //
              .setCurve(Transpose.of(Tensors.of(domain, result)), false) //
              .render(geometricLayer, graphics);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  }

  public static void main(String[] args) {
    new R1RadialBasisFunctionDemo().setVisible(1000, 800);
  }
}
