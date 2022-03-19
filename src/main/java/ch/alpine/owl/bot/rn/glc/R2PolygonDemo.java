// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;

/* package */ class R2PolygonDemo extends R2BaseDemo {
  @Override // from R2BaseDemo
  protected Region<Tensor> region() {
    return new PolygonRegion(R2ExamplePolygons.BULKY_TOP_LEFT);
  }

  @Override // from R2BaseDemo
  protected Tensor startState() {
    return Array.zeros(2);
  }

  public static void main(String[] args) {
    new R2PolygonDemo().start();
  }
}
