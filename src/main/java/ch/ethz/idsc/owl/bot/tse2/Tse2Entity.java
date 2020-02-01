// code by jph, ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Clip;

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
