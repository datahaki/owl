// code by jph
package ch.alpine.owl.bot.delta;

import java.util.Collection;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class DeltaFlowsTest extends TestCase {
  /** the constants define the control */
  private static final Scalar U_NORM = RealScalar.of(0.6);
  /** resolution of radial controls */
  private static final int U_SIZE = 15;

  public void testSimple() {
    Collection<Tensor> controls = new DeltaFlows(U_NORM).getFlows(U_SIZE);
    Scalar u_norm = DeltaControls.maxSpeed(controls);
    Chop._10.requireClose(u_norm, U_NORM);
  }
}
