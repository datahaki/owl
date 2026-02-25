// code by jph
package ch.alpine.owl.order;

import java.io.Serializable;

import ch.alpine.owlets.math.order.BinaryRelation;
import ch.alpine.owlets.math.order.Order;
import ch.alpine.owlets.math.order.OrderComparator;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;

/** Reference:
 * Hauke, Markiewicz, 1994 */
public enum LoewnerPartialOrder {
  ;
  public static final OrderComparator<Tensor> INSTANCE = new Order<>( //
      (BinaryRelation<Tensor> & Serializable) //
      (x, y) -> PositiveSemidefiniteMatrixQ.ofHermitian(x.subtract(y)));
}
