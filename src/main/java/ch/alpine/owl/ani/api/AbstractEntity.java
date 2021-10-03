// code by jph
package ch.alpine.owl.ani.api;

import java.util.NavigableSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

import ch.alpine.java.win.RenderInterface;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** universal entity subject to
 * 1) trajectory based control, {@link TrajectoryEntity}
 * 2) manual control e.g. via joystick
 * 3) passive motion */
public abstract class AbstractEntity implements RenderInterface, AnimationInterface {
  private final EpisodeIntegrator episodeIntegrator;
  private final NavigableSet<EntityControl> entityControls = //
      new ConcurrentSkipListSet<>(EntityControlComparator.INSTANCE);

  // TODO JPH possibly pass in fallback control as argument? to avoid throw in integrate
  protected AbstractEntity(EpisodeIntegrator episodeIntegrator) {
    this.episodeIntegrator = episodeIntegrator;
  }

  protected final void add(EntityControl entityControl) {
    entityControls.add(entityControl);
  }

  @Override
  public final synchronized void integrate(Scalar now) {
    for (EntityControl entityControl : entityControls) {
      Optional<Tensor> u = entityControl.control(getStateTimeNow(), now);
      if (u.isPresent()) {
        episodeIntegrator.move(u.orElseThrow(), now);
        return;
      }
    }
    throw new RuntimeException("control missing");
  }

  public final StateTime getStateTimeNow() {
    return episodeIntegrator.tail();
  }
}
