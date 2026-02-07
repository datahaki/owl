// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.sophis.crv.d2.alg.PolygonRegion;
import ch.alpine.sophus.math.api.Region;
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

  static void main() {
    new R2PolygonDemo().start();
  }
}
