// code by jph
package ch.alpine.owl.region;

import java.io.Serializable;
import java.util.function.Function;

import ch.alpine.tensor.Scalar;

@FunctionalInterface
public interface ScalarMapper<T> extends Function<Scalar, T>, Serializable {
  // ---
}
