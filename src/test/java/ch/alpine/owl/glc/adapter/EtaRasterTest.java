// code by jph
package ch.alpine.owl.glc.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;

class EtaRasterTest {
  @Test
  void testState() {
    StateTimeRaster stateTimeRaster = EtaRaster.state(Tensors.vector(2., 3.));
    Tensor tensor = stateTimeRaster.convertToKey(new StateTime(Tensors.vector(100, 100), RealScalar.of(978667)));
    assertEquals(tensor, Tensors.vector(200, 300));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testJoined() throws ClassNotFoundException, IOException {
    StateTimeRaster stateTimeRaster = EtaRaster.joined(Tensors.vector(2., 3., 4.));
    Tensor tensor = stateTimeRaster.convertToKey(new StateTime(Tensors.vector(100, 100), RealScalar.of(97)));
    assertEquals(tensor, Tensors.vector(200, 300, 97 * 4));
    ExactTensorQ.require(tensor);
    EtaRaster etaRaster = Serialization.copy((EtaRaster) stateTimeRaster);
    assertEquals(etaRaster.eta(), Tensors.vector(2, 3, 4));
  }

  @Test
  void testExtract() {
    StateTimeRaster stateTimeRaster = new EtaRaster(Tensors.vector(1.5), st -> st.state().extract(1, 2));
    Tensor tensor = stateTimeRaster.convertToKey(new StateTime(Tensors.vector(100, 100), RealScalar.of(97)));
    assertEquals(tensor, Tensors.vector(150));
    ExactTensorQ.require(tensor);
  }

  @Test
  void testFail() {
    EtaRaster.timeDependent(Tensors.vector(1, 2), RealScalar.of(1), StateTime::joined);
    assertThrows(Exception.class, () -> EtaRaster.timeDependent(Tensors.vector(1, 2), RealScalar.of(1.), StateTime::joined));
  }
}
