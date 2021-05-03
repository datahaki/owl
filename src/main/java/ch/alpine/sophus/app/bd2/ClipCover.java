// code by jph
package ch.alpine.sophus.app.bd2;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ enum ClipCover {
  ;
  /** @param clip
   * @param scalar
   * @return */
  public static Clip of(Clip clip, Scalar scalar) {
    return Clips.interval( //
        Min.of(clip.min(), scalar), //
        Max.of(clip.max(), scalar));
  }
}
