// code by jph
package ch.alpine.owl.math.order;

import java.util.Comparator;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;

/** lexicographic comparator for vectors
 * 
 * anti-symmetric bifunction: when the input parameters are swapped, the return value flips sign
 * 
 * returns -1 if vector1 < vector2 */
public enum VectorLexicographic implements Comparator<Tensor> {
  COMPARATOR;

  @Override // from Comparator
  public int compare(Tensor vector1, Tensor vector2) {
    // check of entire input is required because the comparison
    // afterwards might be determined before all pairs are visited.
    VectorQ.requireLength(vector1, VectorQ.require(vector2).length());
    int cmp = 0;
    for (int index = 0; index < vector1.length() && cmp == 0; ++index)
      cmp = Scalars.compare(vector1.Get(index), vector2.Get(index));
    return cmp;
  }
}
