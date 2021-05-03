// code by jph
package ch.alpine.owl.bot.rice;

import java.util.List;

import ch.alpine.owl.ani.api.AbstractCircularEntity;
import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.gui.ren.TreeRender;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2NormSquared;

/* package */ abstract class RiceBaseEntity extends AbstractCircularEntity implements GlcPlannerCallback {
  protected final TreeRender treeRender = new TreeRender();

  public RiceBaseEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
  }

  @Override
  public final Scalar distance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y); // non-negative
  }

  @Override
  public final void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    treeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }
}
