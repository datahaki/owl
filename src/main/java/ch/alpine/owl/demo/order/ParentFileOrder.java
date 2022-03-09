// code by jph
package ch.alpine.owl.demo.order;

import java.io.File;

import ch.alpine.owl.math.order.Order;
import ch.alpine.owl.math.order.OrderComparator;

// TODO OWL ALG not sure if partial order, or preorder
public enum ParentFileOrder {
  ;
  public static final OrderComparator<File> INSTANCE = new Order<>(ParentFileRelation.INSTANCE);
}
