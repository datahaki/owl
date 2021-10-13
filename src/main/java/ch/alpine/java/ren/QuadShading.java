// code by jph
package ch.alpine.java.ren;

import java.util.Optional;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.Cross;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.VectorAngle;
import ch.alpine.tensor.sca.Clips;

/* package */ enum QuadShading {
  ANGLE {
    @Override
    public Scalar map(Tensor po, Tensor p0, Tensor p1, Tensor pd) {
      Optional<Scalar> optional = VectorAngle.of(p0.subtract(po), p1.subtract(po));
      return optional.map(Pi.VALUE::under).orElse(RealScalar.ZERO);
    }
  },
  VOLUME {
    private final Scalar factor = RealScalar.of(0.1);

    @Override
    public Scalar map(Tensor po, Tensor p0, Tensor p1, Tensor pd) {
      Scalar scalar = Vector2Norm.of(Cross.of(p0.subtract(po), p1.subtract(po))).multiply(factor);
      return Clips.unit().apply(scalar);
    }
  }, //
  ;

  public abstract Scalar map(Tensor po, Tensor p0, Tensor p1, Tensor pd);
}
