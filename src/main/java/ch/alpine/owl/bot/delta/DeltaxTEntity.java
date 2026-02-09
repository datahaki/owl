// code by jph
package ch.alpine.owl.bot.delta;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.sca.Round;

/** class controls delta using {@link StandardTrajectoryPlanner} */
/* package */ class DeltaxTEntity extends DeltaEntity {
  public DeltaxTEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl, ImageGradientInterpolation imageGradientInterpolation) {
    super(episodeIntegrator, trajectoryControl, imageGradientInterpolation);
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.timeDependent(PARTITION_SCALE, fixedStateIntegrator.getTimeStepTrajectory(), StateTime::joined);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    StateTime stateTime = getStateTimeNow();
    graphics.setColor(Color.GRAY);
    graphics.drawString(stateTime.time().maps(Round._3).toString(), 0, 12 * 2);
  }
}
