// code by jph
package ch.alpine.owl.util.ren;

import java.awt.Color;
import java.util.Map;

import ch.alpine.ascona.util.ren.RenderInterface;
import ch.alpine.owl.glc.rl2.RelaxedPriorityQueue;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;

public class DomainQueueMapRender extends DomainRender {
  private static final ColorDataIndexed INTERIOR = ColorDataLists._097.cyclic().deriveWithAlpha(128);

  public static RenderInterface of(Map<Tensor, RelaxedPriorityQueue> map, Tensor eta) {
    return new DomainQueueMapRender(map, eta);
  }

  // ---
  private final Map<Tensor, RelaxedPriorityQueue> map;

  private DomainQueueMapRender(Map<Tensor, RelaxedPriorityQueue> map, Tensor eta) {
    super(Tensor.of(map.keySet().stream().map(Extract2D.FUNCTION).distinct()), eta);
    this.map = map;
  }

  @Override
  public Color color(Tensor key) {
    return INTERIOR.getColor(map.get(key).collection().size() - 1);
  }
}
