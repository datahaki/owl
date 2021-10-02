// code by jph
package ch.alpine.sophus.ply;

import java.util.Iterator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.lie.r2.Det2D;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Total;

public enum PolygonCentroid {
  ;
  private static final Scalar _6 = RealScalar.of(6);

  /** @param polygon
   * @return
   * @throws Exception if polygon is empty */
  public static Tensor of(Tensor polygon) {
    if (polygon.length() == 1)
      return VectorQ.requireLength(polygon.get(0), 2);
    if (polygon.length() == 2)
      return VectorQ.requireLength(Mean.of(polygon), 2);
    Tensor prev = Last.of(polygon);
    Tensor contrib = Tensors.empty();
    Iterator<Tensor> iterator = polygon.iterator();
    while (iterator.hasNext()) {
      Tensor next = iterator.next();
      contrib.append(prev.add(next).multiply(Det2D.of(prev, next)));
      prev = next;
    }
    return Total.of(contrib).divide(PolygonArea.of(polygon)).divide(_6);
  }
}
