// code by jph
package ch.alpine.owl.bot.r2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.sca.Sign;

/** for single integrator state space
 * use with {@link EulerIntegrator} */
public class R2Flows implements FlowsInterface, Serializable {
  private final Scalar speed;

  public R2Flows(Scalar speed) {
    this.speed = Sign.requirePositive(speed);
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    if (2 < resolution) {
      List<Tensor> list = new ArrayList<>();
      for (Tensor u : CirclePoints.of(resolution))
        list.add(mapU(u).multiply(speed));
      return list;
    }
    throw new RuntimeException("does not cover plane");
  }

  public Tensor stayPut() {
    return Array.zeros(2);
  }

  protected Tensor mapU(Tensor u) {
    return u;
  }
}
