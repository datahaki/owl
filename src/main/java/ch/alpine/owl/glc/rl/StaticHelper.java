// code by yn, jph
package ch.alpine.owl.glc.rl;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.VectorScalar;
import ch.alpine.owl.math.VectorScalars;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Entrywise;

/* package */ enum StaticHelper {
  ;
  /** @param collection
   * @return tensor with lowest costs entrywise */
  static Optional<Tensor> entrywiseMin(Stream<GlcNode> collection) {
    return collection //
        .map(GlcNode::merit) //
        .map(VectorScalar.class::cast) //
        .map(VectorScalar::vector) //
        .reduce(Entrywise.min());
  }

  // FIXME_YN magic const
  private static final Scalar MERIT_EPS = RationalScalar.of(1, 100);

  static boolean isEqual(GlcNode next, RLDomainQueue domainQueue) {
    // TODO_YN check if close to existing nodes / assert if this is helpful
    Tensor nextMerit = VectorScalars.vector(next.merit());
    return domainQueue.stream() //
        .anyMatch(glcNode -> VectorScalars.vector(glcNode.merit()).subtract(nextMerit).stream() //
            .map(Scalar.class::cast) //
            .allMatch(scalar -> Scalars.lessThan(scalar, MERIT_EPS)));
  }

  /** @param collection
   * @param dimension
   * @return best node in collection along given dimension
   * @throws Exception if collection is empty */
  static GlcNode getMin(Collection<GlcNode> collection, int dimension) {
    return Collections.min(collection, new Comparator<GlcNode>() {
      @Override
      public int compare(GlcNode first, GlcNode second) {
        return Scalars.compare( //
            VectorScalars.at(first.merit(), dimension), //
            VectorScalars.at(second.merit(), dimension));
      }
    });
  }
}
