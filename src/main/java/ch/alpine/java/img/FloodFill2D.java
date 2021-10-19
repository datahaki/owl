// code by jph
package ch.alpine.java.img;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Integers;

/** computes manhatten distance by flood fill */
public class FloodFill2D {
  private static final Tensor DXP = Tensors.vector(+1, 0);
  private static final Tensor DXN = Tensors.vector(-1, 0);
  private static final Tensor DYP = Tensors.vector(0, +1);
  private static final Tensor DYN = Tensors.vector(0, -1);

  /** @param image
   * @param ttl non-negative
   * @param seeds
   * @return distance in exact precision */
  public static Tensor of(Tensor image, int ttl, Set<Tensor> seeds) {
    return new FloodFill2D(image, RealScalar.of(Integers.requirePositiveOrZero(ttl)), seeds).array;
  }

  /** seeds are generated from given tensor using {@link #seeds(Tensor)}
   * 
   * @param image
   * @param ttl non-negative
   * @return distance in exact precision */
  public static Tensor of(Tensor image, int ttl) {
    return of(image, ttl, seeds(image));
  }

  // ---
  private final List<Integer> dimensions;
  private final Tensor array;
  private final Tensor image;
  private Set<Tensor> next;

  private FloodFill2D(Tensor image, Scalar ttl, Set<Tensor> prev) {
    dimensions = Dimensions.of(image);
    array = Array.zeros(dimensions);
    this.image = image;
    {
      next = new HashSet<>();
      for (Tensor seed : prev)
        populate(seed, ttl);
      prev = next;
    }
    while (!prev.isEmpty()) {
      ttl = ttl.subtract(RealScalar.ONE);
      if (Scalars.isZero(ttl))
        break;
      next = new HashSet<>();
      for (Tensor seed : prev) {
        populate(seed.add(DXN), ttl);
        populate(seed.add(DXP), ttl);
        populate(seed.add(DYN), ttl);
        populate(seed.add(DYP), ttl);
      }
      prev = next;
    }
  }

  // point to pixel map similar to ImageRegion
  private void populate(Tensor point, Scalar ttl) {
    // using a hash set to prevent duplicates does not help to speed up
    int c0 = Scalars.intValueExact(point.Get(0));
    if (0 <= c0 && c0 < dimensions.get(0)) {
      int c1 = Scalars.intValueExact(point.Get(1));
      if (0 <= c1 && c1 < dimensions.get(1) && //
          Scalars.isZero(array.Get(c0, c1)) && //
          Scalars.isZero(image.Get(c0, c1))) {
        array.set(ttl, c0, c1);
        next.add(point);
      }
    }
  }

  /** @param tensor of rank 2
   * @return set of coordinates in of zeros in given tensor that have at least
   * one neighbor in obstacle space */
  public static Set<Tensor> seeds(Tensor tensor) {
    Set<Tensor> set = new HashSet<>();
    List<Integer> list = Dimensions.of(tensor);
    for (int c0 = 0; c0 < list.get(0); ++c0)
      for (int c1 = 0; c1 < list.get(1); ++c1)
        if (Scalars.isZero(tensor.Get(c0, c1))) {
          boolean flag = false;
          if (0 < c0 && !flag)
            flag = Scalars.nonZero(tensor.Get(c0 - 1, c1 + 0));
          if (c0 + 1 < list.get(0) && !flag)
            flag = Scalars.nonZero(tensor.Get(c0 + 1, c1 + 0));
          if (0 < c1 && !flag)
            flag = Scalars.nonZero(tensor.Get(c0 + 0, c1 - 1));
          if (c1 + 1 < list.get(1) && !flag)
            flag = Scalars.nonZero(tensor.Get(c0 + 0, c1 + 1));
          if (flag)
            set.add(Tensors.vector(c0, c1));
        }
    return set;
  }
}
