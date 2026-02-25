// code by jph
package ch.alpine.owl.order;

import java.io.Serializable;

import ch.alpine.owlets.math.order.BinaryRelation;
import ch.alpine.owlets.math.order.Order;
import ch.alpine.owlets.math.order.OrderComparator;

/** in the Java language the type hierarchy may not contain cycles
 * 
 * https://en.wikipedia.org/wiki/Subtyping */
/* package */ enum TypeStrictPartialOrder {
  ;
  public static final OrderComparator<Class<?>> INSTANCE = new Order<>( //
      (BinaryRelation<Class<?>> & Serializable) //
      Class::isAssignableFrom);
}
