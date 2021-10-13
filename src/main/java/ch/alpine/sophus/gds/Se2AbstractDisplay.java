// code by jph
package ch.alpine.sophus.gds;

import java.io.Serializable;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.crv.d2.Arrowhead;
import ch.alpine.sophus.decim.LineDistance;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.lie.LieTransport;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

public abstract class Se2AbstractDisplay implements ManifoldDisplay, Serializable {
  private static final Tensor ARROWHEAD = Arrowhead.of(0.2).unmodifiable();

  @Override // from GeodesicDisplay
  public final int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor p) {
    return v -> v.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return ARROWHEAD;
  }

  @Override // from GeodesicDisplay
  public final Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public final Tensor matrixLift(Tensor p) {
    return GfxMatrix.of(p);
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return LieTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Biinvariant metricBiinvariant() {
    return null;
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override // from Object
  public abstract String toString();
}
