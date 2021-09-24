// code by gjoel, jph
package ch.alpine.owl.rrts.adapter;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.opt.nd.NdCenterInterface;

/** Remark: TensorDifference would be sufficient but that would result in an inconvenience. */
public class TransitionNdType implements NdType, Serializable {
  private final TransitionSpace transitionSpace;

  /** @param transitionSpace non-null */
  public TransitionNdType(TransitionSpace transitionSpace) {
    this.transitionSpace = Objects.requireNonNull(transitionSpace);
  }

  @Override // from NdType
  public NdCenterInterface ndCenterTo(Tensor center) {
    return new NdCenterInterface() {
      @Override // from NdCenterInterface
      public Scalar distance(Tensor other) {
        return transitionSpace.connect(other, center).length();
      }

      @Override
      public Scalar distance(NdBox ndBox) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public boolean lessThan(int dimension, Scalar median) {
        // TODO Auto-generated method stub
        return false;
      }
    };
  }

  @Override // from NdType
  public NdCenterInterface ndCenterFrom(Tensor center) {
    return new NdCenterInterface() {
      @Override // from NdCenterInterface
      public Scalar distance(Tensor other) {
        return transitionSpace.connect(center, other).length();
      }

      @Override
      public Scalar distance(NdBox ndBox) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public boolean lessThan(int dimension, Scalar median) {
        // TODO Auto-generated method stub
        return false;
      }
    };
  }
}
