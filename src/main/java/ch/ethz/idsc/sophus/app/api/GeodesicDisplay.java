// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Hint: the interface GeodesicDisplay is intended for use in the demo layer
 * but not in the library functions. */
public interface GeodesicDisplay {
  /** @return */
  GeodesicInterface geodesicInterface();

  int dimensions();

  /** @return polygon to visualize the control point */
  Tensor shape();

  /** @param xya
   * @return control point */
  Tensor project(Tensor xya);

  /** @param p control point
   * @return vector of length 2 with grid coordinates {x, y} */
  Tensor toPoint(Tensor p);

  /** @param p
   * @return operator that maps arbitrary dimension tangent vectors to 2d for display */
  TensorUnaryOperator tangentProjection(Tensor p);

  /** @param p control point
   * @return matrix with dimensions 3 x 3 */
  Tensor matrixLift(Tensor p);

  /** @return lie group if the space is a lie group, or null if function is not applicable */
  LieGroup lieGroup();

  HsExponential hsExponential();

  HsTransport hsTransport();

  /** @return vectorLogManifold, or null if not applicable */
  VectorLogManifold vectorLogManifold();

  /** @param p control point
   * @param q control point
   * @return (pseudo-) distance between given control points p and q
   * @throws Exception if functionality is not supported */
  TensorMetric parametricDistance();

  /** @return whether biinvariant scalar product exists */
  boolean isMetricBiinvariant();

  /** @return biinvariantMean, or null, if geodesic space does not support the computation of an biinvariant mean */
  BiinvariantMean biinvariantMean();

  LineDistance lineDistance();

  /** @param resolution
   * @param tensorScalarFunction
   * @return array of scalar values clipped to interval [0, 1] or DoubleScalar.INDETERMINATE */
  default GeodesicArrayPlot geodesicArrayPlot() {
    throw new UnsupportedOperationException();
  }

  @Override // from Object
  String toString();
}
