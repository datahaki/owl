// code by jph and jl
package ch.alpine.owl.bot.delta;

import ch.alpine.java.ren.RenderInterface;
import ch.alpine.java.ren.VectorFieldRender;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.model.VectorFields;
import ch.alpine.sophus.math.Region;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.Box;

/* package */ enum StaticHelper {
  ;
  private static final int LIMIT = 800;

  public static RenderInterface vectorFieldRender( //
      StateSpaceModel stateSpaceModel, Tensor range, Region<Tensor> region, Scalar factor) {
    Box box = Box.of(range.map(Scalar::zero), range);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(box);
    Tensor points = Tensor.of(RandomSample.stream(randomSampleInterface) //
        .filter(p -> !region.test(p)) //
        .limit(LIMIT));
    return new VectorFieldRender().setUV_Pairs(VectorFields.of(stateSpaceModel, points, Array.zeros(2), factor));
  }
}
