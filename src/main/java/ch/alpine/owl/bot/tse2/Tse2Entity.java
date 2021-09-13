// code by jph, ynager
package ch.alpine.owl.bot.tse2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.gui.ren.TrajectoryRender;
import ch.alpine.owl.gui.ren.TreeRender;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.Clip;

public abstract class Tse2Entity extends TrajectoryEntity implements GlcPlannerCallback {
  /** fixed state integrator is used for planning
   * the time difference between two successive nodes in the planner tree is 4/10 */
  protected final FixedStateIntegrator fixedStateIntegrator;
  // ---
  private final TreeRender treeRender = new TreeRender();
  public final Collection<CostFunction> extraCosts = new LinkedList<>();

  /** @param stateTime initial configuration
   * @param trajectoryControl */
  protected Tse2Entity(Clip v_range, StateTime stateTime, TrajectoryControl trajectoryControl) {
    super(new SimpleEpisodeIntegrator( //
        Tse2StateSpaceModel.INSTANCE, //
        new Tse2Integrator(v_range), //
        stateTime), //
        trajectoryControl);
    fixedStateIntegrator = // node interval == 3/10
        FixedStateIntegrator.create(new Tse2Integrator(v_range), Tse2StateSpaceModel.INSTANCE, RationalScalar.of(1, 10), 3);
    // TODO JPH use tse2 fallback control
    add(FallbackControl.of(Array.zeros(2)));
  }

  protected abstract StateTimeRaster stateTimeRaster();

  protected abstract Tensor shape();

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(trajectoryWrap)) {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(trajectoryWrap.trajectory());
      trajectoryRender.setColor(Color.GREEN);
      trajectoryRender.render(geometricLayer, graphics);
    }
    { // indicate current position
      final StateTime stateTime = getStateTimeNow();
      Color color = new Color(64, 64, 64, 128);
      geometricLayer.pushMatrix(Se2Matrix.of(stateTime.state()));
      graphics.setColor(color);
      graphics.fill(geometricLayer.toPath2D(shape()));
      geometricLayer.popMatrix();
    }
    { // indicate position delay[s] into the future
      Tensor state = getEstimatedLocationAt(delayHint());
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(255, 128, 64, 192));
      graphics.fill(new Rectangle2D.Double(point.getX() - 2, point.getY() - 2, 5, 5));
    }
    // ---
    treeRender.render(geometricLayer, graphics);
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    treeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }
}
