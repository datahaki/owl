// code by jph
package ch.alpine.sophus.gds;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.nrm.Vector2Norm;

/** symmetric positive definite 2 x 2 matrices */
public class S1Display extends SnDisplay {
  public static final ManifoldDisplay INSTANCE = new S1Display();

  // ---
  private S1Display() {
    super(1);
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xy = xya.extract(0, 2);
    Scalar norm = Vector2Norm.of(xy);
    return Scalars.isZero(norm) //
        ? UnitVector.of(2, 0)
        : xy.divide(norm);
  }

  @Override // from GeodesicDisplay
  public TensorUnaryOperator tangentProjection(Tensor p) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor xy) {
    return xy.copy();
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor xy) {
    return GfxMatrix.translation(toPoint(xy));
  }
}
