// code by jph
package ch.alpine.owl.math.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

/** trajectory integration with fixed step size over given time period */
public class FixedStateIntegrator implements StateIntegrator, Serializable {
  /** @param integrator
   * @param timeStep non-negative period of one step
   * @param trajectorySize number of steps
   * @return */
  public static FixedStateIntegrator create( //
      Integrator integrator, StateSpaceModel stateSpaceModel, Scalar timeStep, int trajectorySize) {
    return new FixedStateIntegrator(integrator, stateSpaceModel, timeStep, trajectorySize);
  }

  // ---
  private final Integrator integrator;
  private final StateSpaceModel stateSpaceModel;
  private final Scalar timeStep;
  private final int trajectorySize;

  private FixedStateIntegrator(Integrator integrator, StateSpaceModel stateSpaceModel, Scalar timeStep, int trajectorySize) {
    this.integrator = integrator;
    this.stateSpaceModel = stateSpaceModel;
    this.timeStep = Sign.requirePositive(timeStep);
    this.trajectorySize = trajectorySize;
  }

  @Override // from StateIntegrator
  public List<StateTime> trajectory(StateTime stateTime, Tensor u) {
    final List<StateTime> trajectory = new ArrayList<>();
    StateTime prev = stateTime;
    for (int count = 0; count < trajectorySize; ++count) {
      StateTime next = new StateTime( //
          integrator.step(stateSpaceModel, prev.state(), u, timeStep), //
          prev.time().add(timeStep));
      trajectory.add(next);
      prev = next;
    }
    return trajectory;
  }

  /** @return time advancement of trajectory which is the product of the fine grained
   * time step and the trajectory size */
  public Scalar getTimeStepTrajectory() {
    return timeStep.multiply(RealScalar.of(trajectorySize));
  }
}
