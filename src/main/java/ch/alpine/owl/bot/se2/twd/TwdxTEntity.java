// code by jph
package ch.alpine.owl.bot.se2.twd;

import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;

/** two wheel drive entity with state space augmented with time */
/* package */ class TwdxTEntity extends TwdEntity {
  public TwdxTEntity(TwdDuckieFlows twdConfig, StateTime stateTime) {
    super(stateTime, new TwdTrajectoryControl(), twdConfig); // TODO OWL API choice of traj ctrl was not thorough
  }

  @Override
  public Scalar delayHint() {
    return Quantity.of(1.1, "s");
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.timeDependent(PARTITION_SCALE, FIXED_STATE_INTEGRATOR.getTimeStepTrajectory(), StateTime::joined);
  }
}
