// code by jph and jl
package ch.alpine.owl.bot.delta;

import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.model.VectorFields;
import ch.alpine.owl.util.ren.VectorFieldRender;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

/* package */ enum StaticHelper {
  ;
  private static final int LIMIT = 800;

  public static RenderInterface vectorFieldRender( //
      StateSpaceModel stateSpaceModel, Tensor range, Region<Tensor> region, Scalar factor) {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(range.map(Scalar::zero), range);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(coordinateBoundingBox);
    Tensor points = Tensor.of(RandomSample.stream(randomSampleInterface) //
        .filter(p -> !region.test(p)) //
        .limit(LIMIT));
    return new VectorFieldRender().setUV_Pairs(VectorFields.of(stateSpaceModel, points, Array.zeros(2), factor));
  }
}
