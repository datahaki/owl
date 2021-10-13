// code by jph
package ch.alpine.sophus.gds;

import java.io.Serializable;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.decim.LineDistance;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.hs.MetricBiinvariant;
import ch.alpine.sophus.hs.rpn.RpnManifold;
import ch.alpine.sophus.hs.rpn.RpnMetric;
import ch.alpine.sophus.hs.rpn.RpnRandomSample;
import ch.alpine.sophus.hs.sn.SnFastMean;
import ch.alpine.sophus.hs.sn.SnGeodesic;
import ch.alpine.sophus.lie.LieExponential;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.math.TensorMetric;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.r2.CirclePoints;

/** symmetric positive definite 2 x 2 matrices */
public abstract class RpnDisplay implements ManifoldDisplay, Serializable {
  private static final Tensor CIRCLE = CirclePoints.of(15).multiply(RealScalar.of(0.05)).unmodifiable();
  // ---
  private final int dimensions;

  protected RpnDisplay(int dimensions) {
    this.dimensions = dimensions;
  }

  @Override // from GeodesicDisplay
  public final Geodesic geodesic() {
    return SnGeodesic.INSTANCE; // TODO
  }

  @Override
  public final int dimensions() {
    return dimensions;
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return CIRCLE;
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return null;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return null;
  }

  @Override
  public final HsManifold hsManifold() {
    return RpnManifold.INSTANCE;
  }

  @Override
  public final HsTransport hsTransport() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return RpnMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Biinvariant metricBiinvariant() {
    return MetricBiinvariant.EUCLIDEAN;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return SnFastMean.INSTANCE; // TODO
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override
  public final String toString() {
    return "RP" + dimensions();
  }

  @Override
  public final RandomSampleInterface randomSampleInterface() {
    return RpnRandomSample.of(dimensions());
  }
}
