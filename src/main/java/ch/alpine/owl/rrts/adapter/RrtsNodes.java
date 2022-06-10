// code by jph
package ch.alpine.owl.rrts.adapter;

import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.TransitionCostFunction;
import ch.alpine.sophus.crv.Transition;
import ch.alpine.sophus.crv.TransitionSpace;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;

public enum RrtsNodes {
  ;
  public static void costConsistency( //
      RrtsNode node, //
      TransitionSpace transitionSpace, //
      TransitionCostFunction transitionCostFunction) {
    if (!node.isRoot()) {
      RrtsNode parent = node.parent();
      Scalar tran = node.costFromRoot().subtract(parent.costFromRoot());
      Transition transition = transitionSpace.connect(parent.state(), node.state());
      Scalar tc = transitionCostFunction.cost(parent, transition);
      /* status &= Scalars.isZero(Chop._10.of(tc.subtract(tran)));
       * if (!status)
       * throw TensorRuntimeException.of(tc, tran);
       * status &= parent.costFromRoot().add(tran).equals(node.costFromRoot());
       * if (!status)
       * throw TensorRuntimeException.of(tc, tran); */
      Chop._10.requireClose(tc, tran);
      Chop._10.requireClose(parent.costFromRoot().add(tran), node.costFromRoot());
    }
    for (RrtsNode child : node.children())
      costConsistency(child, transitionSpace, transitionCostFunction);
  }
}
