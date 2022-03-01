// code by jph
package ch.alpine.sophus.demo.aurora;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import ch.alpine.sophus.crv.d2.Curvature2D;
import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.lie.LieTransport;
import ch.alpine.sophus.lie.se2c.Se2CoveringManifold;
import ch.alpine.sophus.lie.so2.So2Lift;
import ch.alpine.sophus.math.Do;
import ch.alpine.sophus.math.TensorIteration;
import ch.alpine.sophus.ref.d1h.HermiteSubdivision;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.qty.QuantityMagnitude;

/* package */ abstract class HermiteArray {
  static final HsManifold HS_EXPONENTIAL = Se2CoveringManifold.INSTANCE;
  static final HsTransport HS_TRANSPORT = LieTransport.INSTANCE;
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
   * @param levels 4*/
  public HermiteArray(String name, Scalar period, int levels) {
    this.levels = Integers.requirePositive(levels);
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
    return Vector1Norm.of(vector);
  }

  final Tensor getMatrix() {
    return matrix;
  }

  abstract Tensor compute(int rows, int cols);

  public static void export(File directory, Tensor matrix) throws IOException {
    directory.mkdir();
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      File file = new File(directory, String.format("%s.png", colorDataGradients));
      Export.of(file, Raster.of(matrix, colorDataGradients));
    }
  }
}
