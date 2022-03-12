// code by jph
package ch.alpine.tensor.demo.nd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.opt.nd.NdCenterInterface;
import ch.alpine.tensor.opt.nd.NdCenters;

public enum CenterNorms {
  _1 {
    @Override
    public NdCenterInterface ndCenterInterface(Tensor center) {
      return NdCenters.VECTOR_1_NORM.apply(center);
    }

    @Override
    protected Tensor shape() {
      return CirclePoints.of(4);
    }
  },
  _2 {
    @Override
    public NdCenterInterface ndCenterInterface(Tensor center) {
      return NdCenters.VECTOR_2_NORM.apply(center);
    }

    @Override
    protected Tensor shape() {
      return CirclePoints.of(45);
    }
  },
  _INF {
    @Override
    public NdCenterInterface ndCenterInterface(Tensor center) {
      return NdCenters.VECTOR_INFINITY_NORM.apply(center);
    }

    @Override
    protected Tensor shape() {
      return Tensors.fromString("{{1,1}, {-1,1}, {-1,-1}, {1,-1}}");
    }
  }, //
  ;

  public abstract NdCenterInterface ndCenterInterface(Tensor center);

  protected abstract Tensor shape();
}
