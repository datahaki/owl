// code by jph
package ch.alpine.owl.order;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.owlets.math.order.BinaryRelation;
import ch.alpine.owlets.math.order.Order;
import ch.alpine.owlets.math.order.OrderComparator;

/** comparison either returns
 * INDIFFERENT in case of equality, or
 * INCOMPARABLE otherwise */
public enum EqualityOrder {
  ;
  public static final OrderComparator<Object> INSTANCE = new Order<>( //
      (BinaryRelation<Object> & Serializable) //
      (x, y) -> x.equals(Objects.requireNonNull(y)));
}
