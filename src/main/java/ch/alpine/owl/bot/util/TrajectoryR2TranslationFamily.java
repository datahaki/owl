// code by jph
package ch.alpine.owl.bot.util;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.hs.r2.R2RigidFamily;
import ch.alpine.sophus.hs.r2.R2TranslationFamily;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.sca.Chop;

public class TrajectoryR2TranslationFamily extends R2TranslationFamily {
  /** @param stateIntegrator
   * @param initial
   * @param flow
   * @return */
  public static R2RigidFamily create(StateIntegrator stateIntegrator, StateTime initial, Tensor flow) {
    List<StateTime> trajectory = stateIntegrator.trajectory(initial, flow);
    return new TrajectoryR2TranslationFamily(trajectory, initial);
  }

  // ---
  private final List<Tensor> list = new ArrayList<>();
  private final Scalar ofs;
  private final Scalar delta;
  private final int limit;

  private TrajectoryR2TranslationFamily(List<StateTime> trajectory, StateTime initial) {
    list.add(Extract2D.FUNCTION.apply(initial.state()));
    trajectory.stream() //
        .map(StateTime::state) //
        .map(Extract2D.FUNCTION) //
        .forEach(list::add);
    ofs = initial.time();
    delta = trajectory.get(0).time().subtract(ofs);
    limit = list.size() - 1;
    // ---
    { // TODO OWL ALG consistency check belongs into test
      Tensor times = Tensor.of(trajectory.stream().map(StateTime::time).map(this::index));
      Chop._10.requireClose(times, Range.of(1, times.length() + 1));
    }
  }

  @Override // from AbstractTranslationFamily
  public Tensor function_apply(Scalar scalar) {
    return list.get(Math.min(Scalars.intValueExact(index(scalar)), limit));
  }

  private Scalar index(Scalar scalar) {
    return scalar.subtract(ofs).divide(delta);
  }
}
