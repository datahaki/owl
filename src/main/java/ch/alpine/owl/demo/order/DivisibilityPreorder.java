// code by jph
package ch.alpine.owl.demo.order;

import java.io.Serializable;

import ch.alpine.owl.math.order.BinaryRelation;
import ch.alpine.owl.math.order.Order;
import ch.alpine.owl.math.order.OrderComparator;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** Preorder for non-zero scalars in exact precision */
public enum DivisibilityPreorder {
  ;
  public static final OrderComparator<Scalar> INSTANCE = new Order<>( //
      (BinaryRelation<Scalar> & Serializable) //
      Scalars::divides);
}
