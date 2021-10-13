// code by astoll
package ch.alpine.owl.math.order;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import ch.alpine.owl.demo.order.ScalarTotalOrder;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** Creates EBO (elimination by objective) tracker for a lexicographic semiorder.
 * The EBO procedure chooses a the "best" element from a given set according to the underlying lexicographic semiorder.
 * Keeps track of the whole set.
 * 
 * For a detailed description of the procedure, see
 * "Multi-Objective Optimization Using Preference Structures", Chapter 6.1 */
public class SetEboTracker<K> extends AbstractEboTracker<K> {
  /** @param slacks
   * @return */
  public static <K> EboTracker<K> withList(Tensor slacks) {
    return new SetEboTracker<>(slacks, new LinkedHashSet<>());
  }

  // ---
  private SetEboTracker(Tensor slacks, Collection<Pair<K>> candidateSet) {
    super(slacks, candidateSet);
  }

  @Override // from AbstractEboTracker
  protected void trim(Pair<K> applicantPair, Collection<Pair<K>> candidateSet, Collection<K> discardedKeys) {
    Iterator<Pair<K>> iterator = candidateSet.iterator();
    while (iterator.hasNext()) {
      Pair<K> currentPair = iterator.next();
      ProductOrderTracker<Scalar> productOrderTracker = new ProductOrderTracker<>(ScalarTotalOrder.INSTANCE);
      for (int index = 0; index < dim; ++index) {
        Scalar x = applicantPair.value().Get(index);
        Scalar y = currentPair.value().Get(index);
        OrderComparison semiorder = semiorderComparators.get(index).compare(x, y); // uses ScalarSlackSemiorder
        OrderComparison productorder = productOrderTracker.digest(x, y); // uses ScalarTotalOrder
        // if x strictly precedes the current object and it is strictly preceding
        // in every coordinate until now, then the current object will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_PRECEDES)) { //
          if (productorder.equals(OrderComparison.STRICTLY_PRECEDES)) {
            discardedKeys.add(currentPair.key());
            iterator.remove();
            break; // leave for loop, continue with while loop
          }
        } else //
        // if x strictly succeeding the current object and it is strictly succeeding
        // in every coordinate until now, then x will be discarded
        if (semiorder.equals(OrderComparison.STRICTLY_SUCCEEDS)) { //
          if (productorder.equals(OrderComparison.STRICTLY_SUCCEEDS)) {
            discardedKeys.add(applicantPair.key());
            return;
          }
        }
      }
    }
    candidateSet.add(applicantPair);
  }
}
