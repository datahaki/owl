// code by gjoel, jph
package ch.ethz.idsc.owl.math.pursuit;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatio;
import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ClothoidPursuit implements PursuitInterface, Serializable {
  /** @param lookAhead trajectory point {px, py, pa} */
  public static PursuitInterface of(Tensor lookAhead) {
    return new ClothoidPursuit(lookAhead);
  }

  // ---
  /** first and last ratio/curvature in curve */
  private final ClothoidTerminalRatio clothoidTerminalRatios;

  private ClothoidPursuit(Tensor lookAhead) {
    clothoidTerminalRatios = ClothoidTerminalRatios.of(lookAhead.map(Scalar::zero), lookAhead);
  }

  @Override // from GeodesicPursuitInterface
  public Optional<Scalar> firstRatio() {
    return Optional.of(clothoidTerminalRatios.head());
  }

  @Override // from GeodesicPursuitInterface
  public Tensor ratios() {
    return Tensors.of( // all other ratios/curvatures lay between these two for reasonable inputs
        clothoidTerminalRatios.head(), //
        clothoidTerminalRatios.tail());
  }
}
