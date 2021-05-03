// code by jph
package ch.alpine.owl.demo.order;

import java.io.Serializable;

import ch.alpine.owl.math.order.BinaryRelation;
import ch.alpine.owl.math.order.Order;
import ch.alpine.owl.math.order.OrderComparator;

/** in the Java language the type hierarchy may not contain cycles
 * 
 * https://en.wikipedia.org/wiki/Subtyping */
/* package */ enum TypeStrictPartialOrder {
  ;
  public static final OrderComparator<Class<?>> INSTANCE = new Order<>( //
      (BinaryRelation<Class<?>> & Serializable) //
      Class::isAssignableFrom);
}
