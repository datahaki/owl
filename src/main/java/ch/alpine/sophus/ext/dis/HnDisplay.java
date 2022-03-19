// code by jph
package ch.alpine.sophus.ext.dis;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.crv.d2.StarPoints;
import ch.alpine.sophus.decim.LineDistance;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.hs.hn.HnBiinvariantMean;
import ch.alpine.sophus.hs.hn.HnGeodesic;
import ch.alpine.sophus.hs.hn.HnManifold;
import ch.alpine.sophus.hs.hn.HnMetric;
import ch.alpine.sophus.hs.hn.HnMetricBiinvariant;
import ch.alpine.sophus.hs.hn.HnTransport;
import ch.alpine.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.alpine.sophus.lie.LieExponential;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;

/** symmetric positive definite 2 x 2 matrices */
public abstract class HnDisplay implements ManifoldDisplay, Serializable {
  private static final Tensor STAR_POINTS = StarPoints.of(6, 0.12, 0.04).unmodifiable();
  protected static final Scalar RADIUS = RealScalar.of(2.5);
  // ---
  private final int dimensions;

  protected HnDisplay(int dimensions) {
    this.dimensions = dimensions;
  }

  @Override // from GeodesicDisplay
  public final Geodesic geodesic() {
    return HnGeodesic.INSTANCE;
  }

  @Override
  public final int dimensions() {
    return dimensions;
  }

  @Override // from GeodesicDisplay
  public final Tensor project(Tensor xya) {
    return HnWeierstrassCoordinate.toPoint(xya.extract(0, dimensions));
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null;
  }

  @Override // from GeodesicDisplay
  public final Tensor matrixLift(Tensor p) {
    return GfxMatrix.translation(p);
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return STAR_POINTS;
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final LieExponential lieExponential() {
    return null;
  }

  @Override
  public final HsManifold hsManifold() {
    return HnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return HnTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return HnMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Biinvariant metricBiinvariant() {
    return HnMetricBiinvariant.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return HnBiinvariantMean.of(Chop._08);
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final RandomSampleInterface randomSampleInterface() {
    Distribution distribution = UniformDistribution.of(RADIUS.negate(), RADIUS);
    return new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        // return VectorQ.requireLength(RandomVariate.of(distribution, random, 2).append(RealScalar.ZERO), 3);
        return HnWeierstrassCoordinate.toPoint(RandomVariate.of(distribution, random, dimensions));
      }
    };
  }

  @Override
  public final String toString() {
    return "H" + dimensions();
  }
}
