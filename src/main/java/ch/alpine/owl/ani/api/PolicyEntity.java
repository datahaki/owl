// code by jph
package ch.alpine.owl.ani.api;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.tensor.Tensor;

// TODO JPH first API draft, unify with se2entity and abstract entity
public abstract class PolicyEntity implements AnimationInterface, RenderInterface {
  protected EpisodeIntegrator episodeIntegrator;
  public TrajectoryRegionQuery obstacleQuery = null;

  /** @param stateTime
   * @return state of discrete model */
  public abstract Tensor represent(StateTime stateTime);

  public final StateTime getStateTimeNow() {
    return episodeIntegrator.tail();
  }

  protected abstract Tensor shape();

  private boolean obstacleQuery_isDisjoint(StateTime stateTime) {
    return Objects.isNull(obstacleQuery) || !obstacleQuery.test(stateTime);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    { // indicate current position
      final StateTime stateTime = getStateTimeNow();
      geometricLayer.pushMatrix(GfxMatrix.of(stateTime.state()));
      graphics.fill(geometricLayer.toPath2D(shape()));
      geometricLayer.popMatrix();
    }
    { // draw mouse
      // FIXME this is the wrong place to draw the mouse!
      // Color color = new Color(0, 128, 255, 192);
      // Tensor xya = geometricLayer.getMouseSe2State();
      // StateTime stateTime = new StateTime(xya, getStateTimeNow().time());
      // if (!obstacleQuery_isDisjoint(stateTime))
      // color = new Color(255, 96, 96, 128);
      // geometricLayer.pushMatrix(Se2Matrix.of(xya));
      // graphics.setColor(color);
      // graphics.fill(geometricLayer.toPath2D(shape()));
      // geometricLayer.popMatrix();
    }
  }
}
