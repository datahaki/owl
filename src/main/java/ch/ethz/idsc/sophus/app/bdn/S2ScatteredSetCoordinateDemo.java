// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class S2ScatteredSetCoordinateDemo extends A2ScatteredSetCoordinateDemo {
  private final JToggleButton jToggleLower = new JToggleButton("lower");

  public S2ScatteredSetCoordinateDemo(List<GeodesicDisplay> list, //
      List<LogWeighting> array) {
    super(true, list, array);
    {
      timerFrame.jToolBar.add(jToggleLower);
    }
    // example in pdf
    setControlPointsSe2(Tensors.fromString( //
        "{{-0.293, 0.473, 0.000}, {0.613, 0.703, 0.000}, {0.490, -0.287, 0.000}, {-0.023, -0.693, 0.000}, {-0.713, 0.127, -0.524}, {0.407, 0.357, -0.524}, {0.000, -0.030, -0.524}, {0.233, -0.443, -0.524}}"));
    setControlPointsSe2(Tensors.fromString( //
        "{{-0.423, 0.823, 0}, {0.823, 0.450, 0}, {-0.450, 0.330, 0}, {0.007, 0.003, 0}, {-0.487, -0.240, 0}, {0.583, -0.490, 0}, {-0.003, -0.753, 0}}"));
    setControlPointsSe2(Tensors.fromString( //
        "{{-0.423, 0.823, 0.000}, {0.663, 0.400, 0.000}, {-0.450, 0.330, 0.000}, {0.007, -0.007, 0.000}, {-0.487, -0.240, 0.000}, {0.197, -0.660, 0.000}, {0.733, -0.650, 0.000}, {-0.097, -0.940, 0.000}}"));
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(500, 500);
    {
      boolean lower = getControlPointsSe2().stream().anyMatch(r -> Sign.isNegative(r.Get(2)));
      jToggleLower.setSelected(lower);
    }
    recompute();
  }

  @Override // from ExportCoordinateDemo
  public final Tensor compute(TensorUnaryOperator tensorUnaryOperator, int refinement) {
    Tensor sX = Subdivide.of(-1.0, +1.0, refinement);
    Tensor sY = Subdivide.of(+1.0, -1.0, refinement);
    int n = sX.length();
    boolean lower = jToggleLower.isSelected();
    final Tensor origin = getGeodesicControlPoints();
    Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, lower ? n * 2 : n, n, origin.length());
    Predicate<Tensor> predicate = isRenderable();
    IntStream.range(0, n).parallel().forEach(c0 -> {
      Scalar x = sX.Get(c0);
      int c1 = 0;
      for (Tensor y : sY) {
        Optional<Tensor> optionalP = S2GeodesicDisplay.optionalZ(Tensors.of(x, y, RealScalar.ONE));
        if (optionalP.isPresent()) {
          Tensor point = optionalP.get();
          if (predicate.test(point)) {
            wgs.set(tensorUnaryOperator.apply(point), c1, c0);
            if (lower) {
              point.set(Scalar::negate, 2);
              wgs.set(tensorUnaryOperator.apply(point), n + c1, c0);
            }
          }
        }
        ++c1;
      }
    });
    return wgs;
  }

  Predicate<Tensor> isRenderable() {
    return point -> true;
  }
}
