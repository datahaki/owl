// code by jph
package ch.alpine.owl.demo.order;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.owl.math.order.BinaryRelation;
import ch.alpine.owl.math.order.Order;
import ch.alpine.owl.math.order.OrderComparator;

/** comparison either returns
 * INDIFFERENT in case of equality, or
 * INCOMPARABLE otherwise */
public enum EqualityOrder {
  ;
  public static final OrderComparator<Object> INSTANCE = new Order<>( //
      (BinaryRelation<Object> & Serializable) //
      (x, y) -> x.equals(Objects.requireNonNull(y)));
}
