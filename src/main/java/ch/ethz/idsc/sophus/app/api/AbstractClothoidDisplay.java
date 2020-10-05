// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.sophus.ply.PolygonNormalize;
import ch.ethz.idsc.sophus.ply.Spearhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO probably obsolete: instead use Se2 and Se2Covering with different clothoid builders
public abstract class AbstractClothoidDisplay implements GeodesicDisplay, Serializable {
  private static final long serialVersionUID = 8908586069433981904L;
  private static final Tensor SPEARHEAD = PolygonNormalize.of( //
      Spearhead.of(Tensors.vector(-0.217, -0.183, 4.189), RealScalar.of(0.1)), RealScalar.of(0.08));
  private static final Tensor ARROWHEAD = Arrowhead.of(0.2).unmodifiable();

  @Override
  public abstract ClothoidBuilder geodesicInterface();

  @Override // from GeodesicDisplay
  public final int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return SPEARHEAD;
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor p) {
    return v -> v.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public final Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public final Tensor matrixLift(Tensor p) {
    return Se2Matrix.of(p);
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final HsExponential hsExponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final VectorLogManifold vectorLogManifold() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return (p, q) -> geodesicInterface().curve(p, q).length();
  }

  @Override // from GeodesicDisplay
  public final boolean isMetricBiinvariant() {
    return false;
  }

  @Override
  public final LineDistance lineDistance() {
    return null; // TODO line distance
  }

  @Override // from Object
  public abstract String toString();
}
