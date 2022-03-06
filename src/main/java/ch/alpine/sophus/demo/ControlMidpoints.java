// code by jph
package ch.alpine.sophus.demo;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

import ch.alpine.sophus.api.MidpointInterface;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;

/** creates sequence of end points and midpoints
 * 
 * {1, 2, 3} -> {1, 3/2, 5/2, 3} */
/* package */ class ControlMidpoints implements CurveSubdivision, Serializable {
  /** @param midpointInterface
   * @return */
  public static CurveSubdivision of(MidpointInterface midpointInterface) {
    return new ControlMidpoints(Objects.requireNonNull(midpointInterface));
  }

  // ---
  private final MidpointInterface midpointInterface;

  private ControlMidpoints(MidpointInterface midpointInterface) {
    this.midpointInterface = midpointInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    throw new UnsupportedOperationException();
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    Tensor result = Tensors.reserve(tensor.length() + 1);
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    result.append(prev);
    while (iterator.hasNext())
      result.append(midpointInterface.midpoint(prev, prev = iterator.next()));
    result.append(Last.of(tensor));
    return result;
  }
}
