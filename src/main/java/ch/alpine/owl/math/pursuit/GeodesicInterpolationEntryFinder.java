// code by gjoel
package ch.alpine.owl.math.pursuit;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.sophus.itp.GeodesicInterpolation;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.itp.BinaryAverage;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public class GeodesicInterpolationEntryFinder extends TrajectoryEntryFinder implements Serializable {
  private final BinaryAverage binaryAverage;

  /** @param binaryAverage non-null */
  public GeodesicInterpolationEntryFinder(BinaryAverage binaryAverage) {
    this.binaryAverage = Objects.requireNonNull(binaryAverage);
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar index) {
    Clip clip = Clips.positive(waypoints.length() - 1);
    return new TrajectoryEntry(clip.isInside(index) //
        ? GeodesicInterpolation.of(binaryAverage, waypoints).at(index)
        : null, index);
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    return IntStream.range(0, waypoints.length()).mapToObj(RealScalar::of);
  }
}
