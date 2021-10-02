// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.owl.bot.r2.R2ExamplePolygons;
import ch.alpine.owl.math.region.PolygonRegion;
import ch.alpine.owl.math.region.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;

/* package */ class R2PolygonDemo extends R2BaseDemo {
  @Override // from R2BaseDemo
  protected Region<Tensor> region() {
    return PolygonRegion.numeric(R2ExamplePolygons.BULKY_TOP_LEFT);
  }

  @Override // from R2BaseDemo
  protected Tensor startState() {
    return Array.zeros(2);
  }

  public static void main(String[] args) {
    new R2PolygonDemo().start();
  }
}
