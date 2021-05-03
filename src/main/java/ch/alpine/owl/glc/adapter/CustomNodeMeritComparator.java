// code by ynager
package ch.alpine.owl.glc.adapter;

import java.util.Comparator;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.VectorScalar;
import ch.alpine.owl.math.VectorScalars;
import ch.alpine.tensor.Tensor;

/** compare two nodes based on {@link GlcNode#merit()}
 * the merit is required to be of type {@link VectorScalar} */
public class CustomNodeMeritComparator implements Comparator<GlcNode> {
  private final Comparator<Tensor> comparator;

  public CustomNodeMeritComparator(Comparator<Tensor> comparator) {
    this.comparator = comparator;
  }

  @Override // from Comparator
  public int compare(GlcNode glcNode1, GlcNode glcNode2) {
    return comparator.compare( //
        VectorScalars.vector(glcNode1.merit()), //
        VectorScalars.vector(glcNode2.merit()));
  }
}
