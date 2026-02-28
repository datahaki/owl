// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.ani.adapter.TemporalTrajectoryControl;
import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.StateTimeCoordinateWrap;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
class CarxTEntity extends CarEntity {
  CarxTEntity(StateTime stateTime) {
    super(stateTime, TemporalTrajectoryControl.createInstance(), PARTITION_SCALE, CARFLOWS, SHAPE);
  }

  @Override
  public Scalar delayHint() {
    return Quantity.of(2, "s");
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.timeDependent( //
        partitionScale, FIXED_STATE_INTEGRATOR.getTimeStepTrajectory(), new StateTimeCoordinateWrap(Se2Wrap.INSTANCE));
  }
}
