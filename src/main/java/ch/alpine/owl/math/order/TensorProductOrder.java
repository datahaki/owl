// code by astoll
package ch.alpine.owl.math.order;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.owl.demo.order.ScalarTotalOrder;

public enum TensorProductOrder {
  ;
  /** @param length
   * @return product order comparator for vectors of given length,
   * scalar entries are compared using ScalarTotalOrder */
  public static ProductOrderComparator comparator(int length) {
    return new ProductOrderComparator( //
        Stream.generate(() -> ScalarTotalOrder.INSTANCE) //
            .limit(length) //
            .collect(Collectors.toList()));
  }
}
