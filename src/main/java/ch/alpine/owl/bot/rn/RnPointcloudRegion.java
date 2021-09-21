// code by jph
package ch.alpine.owl.bot.rn;

import java.io.Serializable;
import java.util.Collection;

import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.region.Regions;
import ch.alpine.sophus.math.MinMax;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.EuclideanNdCenter;
import ch.alpine.tensor.opt.nd.NdCenterInterface;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;
import ch.alpine.tensor.opt.nd.NearestNdCluster;
import ch.alpine.tensor.sca.Sign;

public class RnPointcloudRegion implements Region<Tensor>, Serializable {
  /** Example:
   * The points of a point cloud in the 2-dimensional plane are encoded as a N x 2 matrix.
   * 
   * @param points, matrix with dimensions N x D
   * @param radius non-negative
   * @return */
  public static Region<Tensor> of(Tensor points, Scalar radius) {
    Sign.requirePositiveOrZero(radius);
    return Tensors.isEmpty(points) //
        ? Regions.emptyRegion()
        : new RnPointcloudRegion(points, radius);
  }

  /***************************************************/
  private final Tensor points;
  private final Scalar radius;
  private final NdMap<Void> ndMap;

  /** @param points non-empty
   * @param radius */
  private RnPointcloudRegion(Tensor points, Scalar radius) {
    this.points = points.unmodifiable();
    this.radius = radius;
    MinMax minMax = MinMax.of(points);
    ndMap = NdTreeMap.of(minMax.min(), minMax.max(), 5); // magic const
    for (Tensor point : points)
      ndMap.add(point, null);
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    NdCenterInterface distanceInterface = EuclideanNdCenter.of(tensor);
    Collection<NdMatch<Void>> collection = NearestNdCluster.of(ndMap, distanceInterface, 1);
    Scalar distance = collection.iterator().next().distance();
    return Scalars.lessEquals(distance, radius);
  }

  public Tensor points() {
    return points;
  }

  public Scalar radius() {
    return radius;
  }
}
