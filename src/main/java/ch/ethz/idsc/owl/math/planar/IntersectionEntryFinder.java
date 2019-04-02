package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class IntersectionEntryFinder implements TrajectoryEntryFinder {
  private final Scalar distance;

  public IntersectionEntryFinder(Scalar distance) {
    this.distance = distance;
  }

  @Override // from TrajectoryEntryFinder
  public Optional<Tensor> apply(Optional<Tensor> waypoints) {
    if (waypoints.isPresent()) {
      Tensor waypoints_ = Tensor.of(waypoints.get().stream().map(t -> t.extract(0, 2)));
      return new CircleCurveIntersection(distance).string(waypoints_);
    }
    return Optional.empty();
  }
}
