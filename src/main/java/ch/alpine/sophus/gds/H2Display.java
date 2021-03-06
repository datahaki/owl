// code by jph
package ch.alpine.sophus.gds;

import ch.alpine.tensor.Tensor;

public class H2Display extends HnDisplay {
  public static final ManifoldDisplay INSTANCE = new H2Display();

  /***************************************************/
  private H2Display() {
    super(2);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public GeodesicArrayPlot geodesicArrayPlot() {
    return new H2ArrayPlot(RADIUS);
  }
}
