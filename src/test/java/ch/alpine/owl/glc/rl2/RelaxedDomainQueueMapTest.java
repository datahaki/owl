// code by jph
package ch.alpine.owl.glc.rl2;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.math.VectorScalar;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class RelaxedDomainQueueMapTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    RelaxedDomainQueueMap relaxedDomainQueueMap = Serialization.copy(new RelaxedDomainQueueMap(Tensors.vector(1, 1, 1, 1)));
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(11, 2, 3), RealScalar.ZERO), x -> VectorScalar.of(1, 2, 3, 5));
    relaxedDomainQueueMap.addToDomainMap(Tensors.vector(1, 2), glcNode);
  }
}
