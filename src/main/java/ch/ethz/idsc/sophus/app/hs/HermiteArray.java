// code by jph
package ch.ethz.idsc.sophus.app.hs;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.crv.Curvature2D;
import ch.ethz.idsc.sophus.crv.hermite.HermiteSubdivision;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.sophus.lie.so2.So2Lift;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ abstract class HermiteArray {
  static final LieGroup LIE_GROUP = Se2CoveringGroup.INSTANCE;
  static final LieExponential LIE_EXPONENTIAL = Se2CoveringExponential.INSTANCE;
  // private static final BiinvariantMean BIINVARIANT_MEAN = Se2CoveringBiinvariantMean.INSTANCE;
  static final Function<Scalar, ? extends Tensor> FUNCTION = ColorDataGradients.JET;
  private static final int ROWS = 135 * 8;
  private static final int COLS = 240 * 8;
  // ---
  private final int levels;
  private final File folder;
  private final Tensor control = Tensors.empty();
  private final Scalar delta;
  private final Tensor matrix;

  /** @param name "20190701T163225_01"
   * @param period 1/2[s]
   * @param levels 4
   * @throws IOException */
  public HermiteArray(String name, Scalar period, int levels) throws IOException {
    this.levels = levels;
    folder = HomeDirectory.Documents(name);
    folder.mkdir();
    Tensor data = GokartPoseDataV2.INSTANCE.getPoseVel(name, 1_000);
    data.set(new So2Lift(), Tensor.ALL, 0, 2);
    Scalar rate = GokartPoseDataV2.INSTANCE.getSampleRate();
    delta = QuantityMagnitude.SI().in("s").apply(period);
    int skip = Scalars.intValueExact(period.multiply(rate));
    for (int index = 0; index < data.length(); index += skip)
      control.append(data.get(index));
    matrix = compute(ROWS, COLS);
  }

  final Scalar process(HermiteSubdivision hermiteSubdivision) {
    TensorIteration tensorIteration = hermiteSubdivision.string(delta, control);
    Tensor refined = Do.of(tensorIteration::iterate, levels);
    Tensor vector = Curvature2D.string(Tensor.of(refined.stream().map(point -> point.get(0).extract(0, 2))));
    // Tensor vector = Differences.of(Tensor.of(refined.stream().map(point -> point.get(1, 1))));
    // return Log.FUNCTION.apply(Norm._1.ofVector(vector).add(RealScalar.ONE));
    // Tensor vector = Flatten.of(Differences.of(Tensor.of(refined.stream().map(point -> point.get(1)))));
    // return Norm._1.ofVector(Differences.of(vector));
    return Norm._1.ofVector(vector);
  }

  final Tensor getMatrix() {
    return matrix;
  }

  abstract Tensor compute(int rows, int cols);

  public static void export(File directory, Tensor matrix) throws IOException {
    directory.mkdir();
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      File file = new File(directory, String.format("%s.png", colorDataGradients));
      Export.of(file, ArrayPlot.of(matrix, colorDataGradients));
    }
  }
}
