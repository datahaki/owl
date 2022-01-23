// code by bapaden and jph
package ch.alpine.owl.math.state;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Append;

/** StateTime is immutable, contents of instance do not change after construction */
/** @param x the state
 * @param time the time of the state
 * @throws Exception if either of the input parameters is null */
public record StateTime(Tensor state, Scalar time) implements Serializable {
  public StateTime(Tensor state, Scalar time) {
    this.state = state.unmodifiable();
    this.time = Objects.requireNonNull(time);
  }

  /** @return concatenation of state and time as vector */
  public Tensor joined() {
    return Append.of(state, time);
  }

  public String toInfoString() {
    return String.format("t=%s  x=%s", time(), state().toString());
  }
}
