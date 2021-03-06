// code by jph and jl
package ch.alpine.owl.bot.delta;

import ch.alpine.owl.gui.ren.VectorFieldRender;
import ch.alpine.owl.math.VectorFields;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.region.Region;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;

/* package */ enum DeltaHelper {
  ;
  public static VectorFieldRender vectorFieldRender(StateSpaceModel stateSpaceModel, Tensor range, Region<Tensor> region, Scalar factor) {
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface sampler = BoxRandomSample.of(Tensors.vector(0, 0), range);
    Tensor points = Tensor.of(RandomSample.of(sampler, 1000).stream().filter(p -> !region.isMember(p)));
    vectorFieldRender.uv_pairs = //
        VectorFields.of(stateSpaceModel, points, Array.zeros(2), factor);
    return vectorFieldRender;
  }
}
