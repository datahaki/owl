// code by jph
package ch.alpine.owl.bot.r2;

import ch.alpine.java.img.ImageBlackWhiteQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public enum ImageEdges {
  ;
  private static final Scalar MAX = RealScalar.of(256);

  /** @param image with entries equals to 0 or 255
   * @param ttl less than 256
   * @return
   * @throws Exception if image contains entries different from 0 or 255 */
  public static Tensor extrusion(Tensor image, final int ttl) {
    ImageBlackWhiteQ.require(image);
    Scalar factor = MAX.divide(RealScalar.of(ttl + 1));
    return FloodFill2D.of(image, ttl).multiply(factor).add(image);
  }
}
