// code by astoll
package ch.alpine.owl.demo.order;

import java.util.Collection;

import ch.alpine.owl.math.order.Order;
import ch.alpine.owl.math.order.OrderComparator;

/** Creates partial ordering of sets.
 * 
 * <p>For two sets x and y, x < y is satisfied if x is a subset of y. */
public enum SetPartialOrder {
  ;
  /** Compares to sets according to their elements
   * 
   * <table>
   * <tr>
   * <th>Relation</th>
   * <th>Explanation</th>
   * </tr>
   * <tr>
   * <td>x = y</td>
   * <td>All elements in x are contained in y and vice versa</td>
   * </tr>
   * <tr>
   * <td>x &le; b</td>
   * <td>x is a subset of y</td>
   * </tr>
   * <tr>
   * <td>x &ge; y</td>
   * <td>y is a subset of x</td>
   * </tr>
   * <tr>
   * <td>Incomparable </td>
   * <td>Neither set is a subset of the other</td>
   * </tr>
   * </table> */
  public static final OrderComparator<Collection<?>> INSTANCE = new Order<>(SubsetQ.RELATION);
}
