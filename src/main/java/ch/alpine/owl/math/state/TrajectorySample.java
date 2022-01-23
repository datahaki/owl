// code by jph
package ch.alpine.owl.math.state;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.tensor.Tensor;

/** container class that bundles information to follow a trajectory
 * 
 * flow may be null */
public record TrajectorySample(StateTime stateTime, Tensor flow) implements Serializable {
  /** @param stateTime
   * @return first entry of a trajectory that does not specify flow */
  public static TrajectorySample head(StateTime stateTime) {
    return new TrajectorySample(stateTime, null);
  }

  /** typically the first state time in a trajectory
   * may not have a flow associated
   * (since there may not be history for the sample)
   * 
   * We return type {@link Optional} to make the application
   * layer aware of the possibility that flow may not be present.
   * 
   * @return Optional.ofNullable(flow) */
  public Optional<Tensor> getFlow() {
    return Optional.ofNullable(flow);
  }

  /** @return info string representation */
  public String toInfoString() {
    String ustring = Objects.isNull(flow) ? "null" : flow.toString();
    return stateTime.toInfoString() + "  u=" + ustring;
  }
}
