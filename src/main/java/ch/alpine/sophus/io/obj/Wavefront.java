// code by jph
package ch.alpine.sophus.io.obj;

import java.util.List;

import ch.alpine.tensor.Tensor;

public interface Wavefront {
  /** @return |V| x 3 matrix of vertices */
  Tensor vertices();

  /** @return |N| x 3 matrix of normals */
  Tensor normals();

  /** @return list of {@link WavefrontObject}s */
  List<WavefrontObject> objects();
}
