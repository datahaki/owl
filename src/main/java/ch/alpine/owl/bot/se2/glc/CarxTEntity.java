// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.ani.adapter.TemporalTrajectoryControl;
import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.StateTimeCoordinateWrap;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
class CarxTEntity extends CarEntity {
  CarxTEntity(StateTime stateTime) {
    super(stateTime, TemporalTrajectoryControl.createInstance(), PARTITION_SCALE, CARFLOWS, SHAPE);
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(2.0);
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.timeDependent( //
        partitionScale, FIXED_STATE_INTEGRATOR.getTimeStepTrajectory(), new StateTimeCoordinateWrap(Se2Wrap.INSTANCE));
  }
}
