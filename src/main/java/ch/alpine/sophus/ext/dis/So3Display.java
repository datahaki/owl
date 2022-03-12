// code by jph
package ch.alpine.sophus.ext.dis;

import java.io.Serializable;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.decim.LineDistance;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.hs.MetricBiinvariant;
import ch.alpine.sophus.lie.LieExponential;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.so.SoGroup;
import ch.alpine.sophus.lie.so.SoTransport;
import ch.alpine.sophus.lie.so3.Rodrigues;
import ch.alpine.sophus.lie.so3.So3BiinvariantMean;
import ch.alpine.sophus.lie.so3.So3Geodesic;
import ch.alpine.sophus.lie.so3.So3Manifold;
import ch.alpine.sophus.lie.so3.So3Metric;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.nrm.Vector2Norm;

/** symmetric positive definite 2 x 2 matrices */
public class So3Display implements ManifoldDisplay, Serializable {
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.4)).unmodifiable();
  private static final Scalar RADIUS = RealScalar.of(7);
  // ---
  public static final ManifoldDisplay INSTANCE = new So3Display(RADIUS);
  // ---
  private final Scalar radius;

  public So3Display(Scalar radius) {
    this.radius = radius;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public Geodesic geodesic() {
    return So3Geodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return TRIANGLE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor axis = xya.divide(radius);
    Scalar norm = Vector2Norm.of(axis);
    if (Scalars.lessThan(RealScalar.ONE, norm))
      axis = axis.divide(norm);
    return Rodrigues.vectorExp(axis);
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor xyz) {
    return Rodrigues.INSTANCE.vectorLog(xyz).extract(0, 2).multiply(radius);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor xyz) {
    return GfxMatrix.translation(toPoint(xyz));
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return SoGroup.INSTANCE;
  }

  @Override
  public LieExponential lieExponential() {
    return So3Manifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsManifold hsManifold() {
    return So3Manifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsTransport hsTransport() {
    return SoTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return So3Metric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Biinvariant metricBiinvariant() {
    return MetricBiinvariant.EUCLIDEAN;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return So3BiinvariantMean.INSTANCE;
  }

  @Override
  public final LineDistance lineDistance() {
    return null; // TODO OWL ALG line distance should be similar to s^3
  }

  @Override // from Object
  public String toString() {
    return "SO3";
  }
}
