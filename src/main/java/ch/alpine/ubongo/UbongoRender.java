// code by jph
package ch.alpine.ubongo;

import java.util.List;
import java.util.stream.Stream;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.CyclicColorDataIndexed;
import ch.alpine.tensor.img.StrictColorDataIndexed;

/* package */ enum UbongoRender {
  ;
  private static final ColorDataIndexed INSTANCE = StrictColorDataIndexed.of(Tensor.of(Stream.of(Ubongo.values()).map(Ubongo::colorVector)));
  private static final ColorDataIndexed MONOCHROME = CyclicColorDataIndexed.of(Tensors.of(Tensors.vector(160, 160, 160, 255)));

  /** @param list
   * @param solution
   * @return */
  public static Tensor matrix(List<Integer> list, List<UbongoEntry> solution) {
    Tensor image = ConstantArray.of(DoubleScalar.INDETERMINATE, list).copy();
    for (UbongoEntry ubongoEntry : solution) {
      List<Integer> size = Dimensions.of(ubongoEntry.stamp);
      for (int si = 0; si < size.get(0); ++si)
        for (int sj = 0; sj < size.get(1); ++sj)
          if (Scalars.nonZero(ubongoEntry.stamp.Get(si, sj)))
            image.set(RealScalar.of(ubongoEntry.ubongo.ordinal()), ubongoEntry.i + si, ubongoEntry.j + sj);
    }
    return image;
  }

  /** @param list
   * @param solution
   * @return */
  public static Tensor of(List<Integer> list, List<UbongoEntry> solution) {
    return matrix(list, solution).map(INSTANCE);
  }

  /** @param list
   * @param solution
   * @return */
  public static Tensor gray(List<Integer> list, List<UbongoEntry> solution) {
    return matrix(list, solution).map(MONOCHROME);
  }
}
