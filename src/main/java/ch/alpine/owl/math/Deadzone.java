// code by edo
// adapted by jph
package ch.alpine.owl.math;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public class Deadzone implements ScalarUnaryOperator {
  public static Deadzone of(Clip clip) {
    return new Deadzone(Objects.requireNonNull(clip));
  }

  public static Deadzone of(Scalar min, Scalar max) {
    return new Deadzone(Clips.interval(min, max));
  }

  public static Deadzone of(Number min, Number max) {
    return new Deadzone(Clips.interval(min, max));
  }

  /***************************************************/
  private final Clip clip;

  private Deadzone(Clip clip) {
    this.clip = clip;
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    return scalar.subtract(clip.apply(scalar));
  }
}
