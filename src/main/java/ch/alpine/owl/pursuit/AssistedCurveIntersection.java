// code by gjoel
package ch.alpine.owl.pursuit;

import java.util.Optional;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.RotateLeft;
import ch.alpine.tensor.itp.BinaryAverage;

public abstract class AssistedCurveIntersection extends SimpleCurveIntersection //
    implements AssistedCurveIntersectionInterface {
  /** @param radius non-negative
   * @param binaryAverage
   * @throws Exception if given radius is negative */
  protected AssistedCurveIntersection(Scalar radius, BinaryAverage binaryAverage) {
    super(radius, binaryAverage);
  }

  @Override // from AssistedCurveIntersectionInterface
  public final Optional<CurvePoint> cyclic(Tensor tensor, int prevIdx) {
    return universal(tensor, prevIdx % tensor.length(), 0);
  }

  @Override // from AssistedCurveIntersectionInterface
  public final Optional<CurvePoint> string(Tensor tensor, int prevIdx) {
    return prevIdx > tensor.length() //
        ? Optional.empty()
        : universal(tensor, prevIdx, 1);
  }

  private Optional<CurvePoint> universal(Tensor tensor, int prevIdx, int first) {
    Optional<CurvePoint> optional = universal(RotateLeft.of(tensor, prevIdx), first);
    return optional.map(curvePoint -> curvePoint.withIndex(Math.floorMod(curvePoint.getIndex() + prevIdx, tensor.length())));
  }
}
