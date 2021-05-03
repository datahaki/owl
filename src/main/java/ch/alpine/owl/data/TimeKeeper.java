// code by jph
package ch.alpine.owl.data;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Timing;

/** TimeKeeper is also used in owly3d */
public class TimeKeeper {
  private final Timing timing = Timing.started();

  public Scalar now() {
    return RealScalar.of(timing.seconds());
  }
}
