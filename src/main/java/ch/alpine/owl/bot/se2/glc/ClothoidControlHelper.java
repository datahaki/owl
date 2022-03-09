// code by gjoel
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public enum ClothoidControlHelper {
  ;
  /** mirror the points along the y axis and invert their orientation
   * 
   * @param se2points curve given by points {x, y, a} */
  public static void mirrorAndReverse(Tensor se2points) {
    se2points.set(Scalar::negate, Tensor.ALL, 0);
    se2points.set(Scalar::negate, Tensor.ALL, 2);
  }
}
