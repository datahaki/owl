// code by jph
package ch.alpine.sophus.demo.clt;

import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilderImpl;
import ch.alpine.sophus.clt.par.ClothoidIntegrations;
import ch.alpine.tensor.Scalar;

public enum CustomClothoidBuilder {
  ;
  public static ClothoidBuilder of(Scalar lambda) {
    return new ClothoidBuilderImpl(CustomClothoidQuadratic.of(lambda), ClothoidIntegrations.ANALYTIC);
  }
}
