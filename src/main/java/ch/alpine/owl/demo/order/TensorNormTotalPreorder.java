// code by jph
package ch.alpine.owl.demo.order;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.owl.math.order.BinaryRelation;
import ch.alpine.owl.math.order.Order;
import ch.alpine.owl.math.order.OrderComparator;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;

/** Total preorder for tensor norms.
 * 
 * a mapping from any set to the reals results in a preorder
 * using Scalars.lessEquals as a binary relation
 * 
 * binary relation that is reflexive and transitive, but not antisymmetric */
public class TensorNormTotalPreorder implements BinaryRelation<Tensor>, Serializable {
  private final TensorScalarFunction tensorScalarFunction;

  public TensorNormTotalPreorder(TensorScalarFunction tensorScalarFunction) {
    this.tensorScalarFunction = Objects.requireNonNull(tensorScalarFunction);
  }

  @Override // from BinaryRelation
  public boolean test(Tensor x, Tensor y) {
    return Scalars.lessEquals(tensorScalarFunction.apply(x), tensorScalarFunction.apply(y));
  }

  /** @return total preorder comparator that never returns INCOMPARABLE */
  public OrderComparator<Tensor> comparator() {
    return new Order<>(this);
  }
}
