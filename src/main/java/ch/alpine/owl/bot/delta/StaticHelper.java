// code by jph and jl
package ch.alpine.owl.bot.delta;

import java.util.function.Predicate;

import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.model.VectorFields;
import ch.alpine.owl.util.ren.VectorFieldRender;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

/* package */ enum StaticHelper {
  ;
  private static final int LIMIT = 800;

  public static RenderInterface vectorFieldRender( //
      StateSpaceModel stateSpaceModel, Tensor range, Predicate<Tensor> region, Scalar factor) {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(range.maps(Scalar::zero), range);
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(coordinateBoundingBox);
    Tensor points = Tensor.of(RandomSample.stream(randomSampleInterface) //
        .filter(Predicate.not(region::test)) //
        .limit(LIMIT));
    return new VectorFieldRender().setUV_Pairs(VectorFields.of(stateSpaceModel, points, Array.zeros(2), factor));
  }
}
