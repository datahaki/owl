// code by jph
package ch.alpine.sophus.gds;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.decim.LineDistance;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.lie.LieExponential;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.math.TensorMetric;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** Hint: the interface GeodesicDisplay is intended for use in the demo layer
 * but not in the library functions. */
public interface ManifoldDisplay {
  /** @return */
  Geodesic geodesicInterface();

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

  LieExponential lieExponential();

  HsManifold hsManifold();

  HsTransport hsTransport();

  /** @param p control point
   * @param q control point
   * @return (pseudo-) distance between given control points p and q
   * @throws Exception if functionality is not supported */
  TensorMetric parametricDistance();

  /** @return metric biinvariant or null if metric is not biinvariant */
  Biinvariant metricBiinvariant();

  /** @return biinvariantMean, or null, if geodesic space does not support the computation of an biinvariant mean */
  BiinvariantMean biinvariantMean();

  LineDistance lineDistance();

  /** @param resolution
   * @param tensorScalarFunction
   * @return array of scalar values clipped to interval [0, 1] or DoubleScalar.INDETERMINATE */
  default GeodesicArrayPlot geodesicArrayPlot() {
    throw new UnsupportedOperationException();
  }

  default RandomSampleInterface randomSampleInterface() {
    throw new UnsupportedOperationException();
  }

  @Override // from Object
  String toString();
}
