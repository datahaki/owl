// code by astoll, jph
package ch.alpine.owl.order;

import java.io.Serializable;

import ch.alpine.owl.math.order.BinaryRelation;
import ch.alpine.owl.math.order.Order;
import ch.alpine.owl.math.order.OrderComparator;

public enum IntegerTotalOrder {
  ;
  public static final OrderComparator<Integer> INSTANCE = new Order<>( //
      (BinaryRelation<Integer> & Serializable) //
      (x, y) -> x <= y);
}
