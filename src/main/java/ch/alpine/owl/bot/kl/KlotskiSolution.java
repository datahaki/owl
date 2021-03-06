// code by jph
package ch.alpine.owl.bot.kl;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Tensor;

/* package */ class KlotskiSolution implements Serializable {
  // ---
  public final KlotskiProblem klotskiProblem;
  public final List<StateTime> list;
  public final Tensor domain;

  public KlotskiSolution(KlotskiProblem klotskiProblem, List<StateTime> list, Tensor domain) {
    this.klotskiProblem = klotskiProblem;
    this.list = list;
    this.domain = domain;
  }
}
