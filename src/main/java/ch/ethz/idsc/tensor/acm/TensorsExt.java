// code by jph
package ch.ethz.idsc.tensor.acm;

import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/** EXPERIMENTAL */
public enum TensorsExt {
  ;
  public static Tensor of(Object... objects) {
    return Tensor.of(Stream.of(objects).map(TensorsExt::fromObject));
  }

  private static Tensor fromObject(Object object) {
    if (object instanceof Tensor)
      return ((Tensor) object).copy();
    if (object instanceof Number)
      return RealScalar.of((Number) object);
    throw new IllegalArgumentException();
  }
}
