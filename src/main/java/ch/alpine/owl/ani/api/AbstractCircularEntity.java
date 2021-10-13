// code by jph
package ch.alpine.owl.ani.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.gui.ren.TrajectoryRender;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public abstract class AbstractCircularEntity extends TrajectoryEntity {
  public AbstractCircularEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(trajectoryWrap)) {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(trajectoryWrap.trajectory());
      trajectoryRender.render(geometricLayer, graphics);
    }
    { // indicate current position
      Tensor state = getStateTimeNow().state();
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(64, 128, 64, 192));
      graphics.fill(new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 7, 7));
    }
    { // indicate position 1[s] into the future
      Tensor state = getEstimatedLocationAt(delayHint());
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(255, 128, 128 - 64, 128 + 64));
      graphics.fill(new Rectangle2D.Double(point.getX() - 2, point.getY() - 2, 5, 5));
    }
    if (Objects.nonNull(trajectoryWrap)) {
      StateTime stateTime = getStateTimeNow();
      Scalar now = stateTime.time();
      if (trajectoryWrap.isDefined(now)) {
        TrajectorySample trajectorySample = trajectoryWrap.getSample(now);
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(stateTime.state(), trajectorySample.stateTime().state()));
        graphics.setColor(Color.PINK);
        graphics.draw(path2d);
      }
    }
  }
}
