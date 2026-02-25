// code by astoll, jph
package ch.alpine.owl.order;

import java.io.Serializable;

import ch.alpine.owlets.math.order.BinaryRelation;
import ch.alpine.owlets.math.order.Order;
import ch.alpine.owlets.math.order.OrderComparator;

public enum IntegerTotalOrder {
  ;
  public static final OrderComparator<Integer> INSTANCE = new Order<>( //
      (BinaryRelation<Integer> & Serializable) //
      (x, y) -> x <= y);
}
