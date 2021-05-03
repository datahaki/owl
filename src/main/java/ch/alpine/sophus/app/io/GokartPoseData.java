// code by jph
package ch.alpine.sophus.app.io;

import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public interface GokartPoseData {
  /** @return available names to use in {@link #getPose(String, int)} */
  List<String> list();

  /** Hint:
   * use limit == Integer.MAX_VALUE for all pose data
   * 
   * @param name
   * @param limit
   * @return matrix of dimensions N x 3 with rows of the form {x, y, heading} without units */
  Tensor getPose(String name, int limit);

  /** @return typically 20[s^-1], or 50[s^-1] */
  Scalar getSampleRate();
}
