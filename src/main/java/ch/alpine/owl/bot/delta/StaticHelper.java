// code by jph and jl
package ch.alpine.owl.bot.delta;

import java.util.function.Predicate;

import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.util.bot.VectorFields;
import ch.alpine.owl.util.ren.VectorFieldRender;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

/* package */ enum StaticHelper {
  ;
  private static final int LIMIT = 800;

  /** @param stateSpaceModel
   * @param range
   * @param region
   * @param factor to map speed to spatial domain
   * @return */
  public static RenderInterface vectorFieldRender( //
      StateSpaceModel stateSpaceModel, Tensor range, Predicate<Tensor> region, Tensor fallback_u, Scalar factor) {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBounds.of(range.maps(Scalar::zero), range);
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(coordinateBoundingBox);
    Tensor points = Tensor.of(RandomSample.stream(randomSampleInterface) //
        .filter(Predicate.not(region::test)) //
        .limit(LIMIT));
    return new VectorFieldRender().setUV_Pairs(VectorFields.of(stateSpaceModel, points, fallback_u, factor));
  }
}
