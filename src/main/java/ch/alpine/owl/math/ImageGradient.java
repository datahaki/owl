// code by jph
package ch.alpine.owl.math;

import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Differences;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.TensorMap;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.Cross;

public enum ImageGradient {
  ;
  /** Example:
   * <pre>
   * Dimensions.of(image) == [179, 128]
   * Dimensions.of(ImageGradient.of(image)) == [178, 127, 2]
   * </pre>
   * 
   * @param image with rank 2
   * @return tensor of rank 3 with height and width reduced by one */
  public static Tensor of(Tensor image) {
    List<Integer> dims = Dimensions.of(image);
    Tensor diffx = Differences.of(image);
    diffx = TensorMap.of(tensor -> tensor.extract(0, dims.get(1) - 1), diffx, 1);
    Tensor diffy = TensorMap.of(Differences::of, image, 1);
    diffy = diffy.extract(0, dims.get(0) - 1);
    return Transpose.of(Tensors.of(diffx, diffy), 2, 0, 1);
  }

  /** @param image with rank 2
   * @return tensor of rank 3 with height and width reduced by one */
  public static Tensor rotated(Tensor image) {
    return TensorMap.of(Cross::of, of(image), 2);
  }
}
