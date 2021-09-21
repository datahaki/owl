// code by jph
package ch.alpine.sophus.opt;

import java.io.Serializable;
import java.util.Collection;

import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.opt.nd.EuclideanNdCenter;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;
import ch.alpine.tensor.opt.nd.NearestNdCluster;
import ch.alpine.tensor.red.Entrywise;

public class NdTreeWeighting implements LogWeighting, Serializable {
  private final int limit;

  public NdTreeWeighting(int limit) {
    this.limit = limit;
  }

  @Override
  public TensorUnaryOperator operator(Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TensorScalarFunction function(Biinvariant biinvariant, VectorLogManifold vectorLogManifold, //
      ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
    Tensor lbounds = Entrywise.min().of(sequence);
    Tensor ubounds = Entrywise.max().of(sequence);
    NdMap<Scalar> ndMap = NdTreeMap.of(lbounds, ubounds, 2);
    for (int index = 0; index < values.length(); ++index)
      ndMap.add(sequence.get(index), values.Get(index));
    return new Inner(ndMap, variogram);
  }

  @Override
  public String toString() {
    return String.format("%s[%d]", NdTreeWeighting.class.getSimpleName(), limit);
  }

  private class Inner implements TensorScalarFunction {
    private final NdMap<Scalar> ndMap;
    private final ScalarUnaryOperator variogram;

    public Inner(NdMap<Scalar> ndMap, ScalarUnaryOperator variogram) {
      this.ndMap = ndMap;
      this.variogram = variogram;
    }

    @Override
    public Scalar apply(Tensor center) {
      Collection<NdMatch<Scalar>> collection = NearestNdCluster.of(ndMap, EuclideanNdCenter.of(center), limit);
      if (collection.isEmpty())
        return DoubleScalar.INDETERMINATE;
      Tensor weights = NormalizeTotal.FUNCTION.apply( //
          Tensor.of(collection.stream().map(NdMatch::distance).map(variogram)));
      return (Scalar) weights.dot(Tensor.of(collection.stream().map(NdMatch::value)));
    }
  }
}
