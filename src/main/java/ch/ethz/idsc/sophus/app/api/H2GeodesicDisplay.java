// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.IterativeBiinvariantMean;
import ch.ethz.idsc.sophus.hs.hn.HnGeodesic;
import ch.ethz.idsc.sophus.hs.hn.HnManifold;
import ch.ethz.idsc.sophus.hs.hn.HnMetric;
import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum H2GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;

  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.2));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return HnGeodesic.INSTANCE;
  }

  @Override
  public int dimensions() {
    return 2;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return TRIANGLE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return HnWeierstrassCoordinate.toPoint(xya.extract(0, 2));
  }

  @Override
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Matrix.translation(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return null;
  }

  @Override // from GeodesicDisplay
  public Exponential exponential() {
    return null;
  }

  @Override
  public HsExponential hsExponential() {
    return HnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public FlattenLogManifold flattenLogManifold() {
    return HnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Scalar parametricDistance(Tensor p, Tensor q) {
    return HnMetric.INSTANCE.distance(p, q);
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return IterativeBiinvariantMean.of(hsExponential());
  }

  @Override // from Object
  public String toString() {
    return "H2";
  }
}
