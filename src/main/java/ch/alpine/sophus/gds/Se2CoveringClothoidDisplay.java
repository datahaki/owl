// code by jph
package ch.alpine.sophus.gds;

import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.tensor.Tensor;

public final class Se2CoveringClothoidDisplay extends AbstractClothoidDisplay {
  public static final ManifoldDisplay INSTANCE = new Se2CoveringClothoidDisplay();

  // ---
  private Se2CoveringClothoidDisplay() {
    // ---
  }

  @Override // from AbstractClothoidDisplay
  public ClothoidBuilder geodesic() {
    return ClothoidBuilders.SE2_COVERING.clothoidBuilder();
  }

  @Override // from GeodesicDisplay
  public final Tensor project(Tensor xya) {
    return xya;
  }

  @Override // from Object
  public String toString() {
    return "ClC";
  }
}
