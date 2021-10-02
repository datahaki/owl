// code by vc
// inspired by https://www.mathopenref.com/coordpolygonarea.html
package ch.alpine.sophus.ply;

import java.util.Iterator;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.lie.r2.Det2D;

/** polygon not necessarily convex
 * 
 * computes signed area circumscribed by given polygon
 * area is positive when polygon is in counter-clockwise direction */
public enum PolygonArea {
  ;
  /** @param polygon
   * @return */
  public static Scalar of(Tensor polygon) {
    if (Tensors.isEmpty(polygon))
      return RealScalar.ZERO;
    Tensor prev = Last.of(polygon);
    Scalar sum = Det2D.of(prev, prev);
    Iterator<Tensor> iterator = polygon.iterator();
    while (iterator.hasNext())
      sum = sum.add(Det2D.of(prev, prev = iterator.next()));
    return sum.multiply(RationalScalar.HALF);
  }
}
