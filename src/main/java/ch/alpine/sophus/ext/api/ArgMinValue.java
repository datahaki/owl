// code by jph
package ch.alpine.sophus.ext.api;

import java.io.Serializable;
import java.util.Optional;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ArgMin;

/** Hint:
 * does not implement {@link Serializable} because {@link Optional} does not implement {@link Serializable} */
/* package */ class ArgMinValue {
  /** @param tensor
   * @return */
  public static ArgMinValue of(Tensor tensor) {
    int index = ArgMin.of(tensor);
    return new ArgMinValue(index, 0 <= index //
        ? Optional.of(tensor.Get(index))
        : Optional.empty());
  }

  // ---
  private final int index;
  private final Optional<Scalar> value;

  private ArgMinValue(int index, Optional<Scalar> value) {
    this.index = index;
    this.value = value;
  }

  public int index() {
    return index;
  }

  public Optional<Integer> index(Scalar threshold) {
    return value.isPresent() //
        && Scalars.lessEquals(value.get(), threshold) //
            ? Optional.of(index)
            : Optional.empty();
  }

  public Optional<Scalar> value() {
    return value;
  }

  public Optional<Scalar> value(Scalar threshold) {
    return value.isPresent() //
        && Scalars.lessEquals(value.get(), threshold) //
            ? value
            : Optional.empty();
  }
}
