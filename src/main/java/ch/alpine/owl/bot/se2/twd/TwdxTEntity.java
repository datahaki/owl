// code by jph
package ch.alpine.owl.bot.se2.twd;

import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

/** two wheel drive entity with state space augmented with time */
/* package */ class TwdxTEntity extends TwdEntity {
  public TwdxTEntity(TwdDuckieFlows twdConfig, StateTime stateTime) {
    super(stateTime, new TwdTrajectoryControl(), twdConfig); // TODO choice of traj ctrl was not thorough
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(1.1);
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.timeDependent(PARTITION_SCALE, FIXED_STATE_INTEGRATOR.getTimeStepTrajectory(), StateTime::joined);
  }
}
