// code by jph and jl
package ch.alpine.owl.bot.delta;

import java.io.Serializable;

import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.tensor.Tensor;

/** an upper bound of the speed of an entity in the river delta is
 * imageGradient.maxNormGradient() + |u_max|
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * Lipschitz L == imageGradientInterpolation.maxNormGradient()
 * 
 * @param imageGradientInterpolation */
public record DeltaStateSpaceModel(ImageGradientInterpolation imageGradientInterpolation) //
    implements StateSpaceModel, Serializable {
  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    return imageGradientInterpolation.get(x).add(u);
  }
}
