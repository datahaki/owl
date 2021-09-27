// code by gjoel, jph
package ch.alpine.owl.rrts.adapter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionCostFunction;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

public class ComboTransitionCostFunction implements TransitionCostFunction, Serializable {
  /** @param transitionCostFunctions
   * @return */
  public static TransitionCostFunction of(TransitionCostFunction... transitionCostFunctions) {
    return new ComboTransitionCostFunction(Arrays.stream(transitionCostFunctions) //
        .collect(Collectors.toMap(f -> f, f -> RealScalar.ONE)));
  }

  /***************************************************/
  private final Map<TransitionCostFunction, Scalar> map;
  // private final int influence;

  /** @param map */
  public ComboTransitionCostFunction(Map<TransitionCostFunction, Scalar> map) {
    this.map = map;
    // influence = map.keySet().stream() //
    // .mapToInt(TransitionCostFunction::influence) //
    // .max() //
    // .getAsInt();
  }

  @Override // from TransitionCostFunction
  public Scalar cost(RrtsNode rrtsNode, Transition transition) {
    return map.entrySet().stream() //
        .map(entry -> entry.getKey().cost(rrtsNode, transition).multiply(entry.getValue())) //
        .reduce(Scalar::add) //
        .orElseThrow();
  }
  // @Override // from TransitionCostFunction
  // public int influence() {
  // return influence;
  // }
}
