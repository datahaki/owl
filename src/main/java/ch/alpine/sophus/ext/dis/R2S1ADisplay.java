// code by jph
package ch.alpine.sophus.ext.dis;

import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.hs.rs.Se2inR2S;

public class R2S1ADisplay extends R2S1AbstractDisplay {
  public static final ManifoldDisplay INSTANCE = new R2S1ADisplay();

  private R2S1ADisplay() {
  }

  @Override
  public Geodesic geodesic() {
    return Se2inR2S.METHOD_0;
  }

  @Override // from GeodesicDisplay
  public String toString() {
    return "R2S1 A";
  }
}
