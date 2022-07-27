// code by astoll
package ch.alpine.owl.math.order;

import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** A semiorder is a special type of strict partial order.
 * 
 * Given the semiorder representation a real-scalar valued function <t>u</t> (called the utility function),
 * which maps the elements on to the reals, and a discrimination threshold (also alled slack and usually
 * denoted by sigma) we can infer the semiorder relation. Two elements <t>x</t> and <t>y</t> are in the
 * relation <t>R</t> which is represent by <t>u</t> and <t>sigma</t> iff:
 * <t>u(x) + sigma less than f(y)</t> */
public enum Semiorder {
  ;
  /** Implements the semiorder where the utility function maps from type T to Scalar.
   * 
   * @param utilityFunction<T, Scalar>
   * @param slack
   * @return OrderComparison.STRICTLY_PRECEDES if u(x) + sigma less than f(y),
   * OrderComparison.STRICTLY_SUCCEDES if u(y) + sigma less than f(x),
   * or OrderComparison.INDIFFERENT if neither of the both. */
  public static <T> OrderComparator<T> comparator(Function<T, Scalar> utilityFunction, Scalar slack) {
    return new OrderComparator<>() {
      @Override // from OrderComparator
      public OrderComparison compare(T x, T y) {
        Scalar utility_x = utilityFunction.apply(x);
        Scalar utility_y = utilityFunction.apply(y);
        if (Scalars.lessThan(utility_x.add(slack), utility_y))
          return OrderComparison.STRICTLY_PRECEDES;
        if (Scalars.lessThan(utility_y.add(slack), utility_x))
          return OrderComparison.STRICTLY_SUCCEEDS;
        return OrderComparison.INDIFFERENT;
      }
    };
  }
}
