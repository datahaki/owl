// code by jph
package ch.alpine.owl.gui.win;

import java.util.ArrayDeque;
import java.util.Deque;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class GeometricLayerTest extends TestCase {
  public void testSimple() {
    Deque<Integer> deque = new ArrayDeque<>();
    deque.push(2);
    deque.push(4);
    deque.push(9);
    assertEquals((int) deque.peek(), 9);
    deque.pop();
    assertEquals((int) deque.peek(), 4);
    deque.pop();
    assertEquals((int) deque.peek(), 2);
    deque.pop();
    assertEquals(deque.peek(), null);
  }

  public void testConstruction() {
    Tensor model2pixel = Tensors.fromString("{{1, 2, 3}, {2, -1, 7}, {0, 0, 1}}");
    Tensor mouseSe2State = Tensors.vector(9, 7, 2);
    GeometricLayer geometricLayer = new GeometricLayer(model2pixel, mouseSe2State);
    geometricLayer.toPoint2D(Tensors.vector(1, 2));
    assertEquals(geometricLayer.getMatrix(), model2pixel);
    geometricLayer.pushMatrix(IdentityMatrix.of(3));
    assertEquals(geometricLayer.getMatrix(), model2pixel);
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
    assertEquals(mouseSe2State, geometricLayer.getMouseSe2State());
    AssertFail.of(() -> geometricLayer.popMatrix());
  }

  public void testVector() {
    Tensor model2pixel = Tensors.fromString("{{1, 2, 3}, {2, -1, 7}, {0, 0, 1}}");
    Tensor mouseSe2State = Tensors.vector(9, 7, 2);
    GeometricLayer geometricLayer = new GeometricLayer(model2pixel, mouseSe2State);
    Tensor vector = Tensors.vector(9, 20, 1);
    Tensor v1 = geometricLayer.toVector(vector);
    Tensor v2 = geometricLayer.toVector(9, 20);
    Tensor expected = model2pixel.dot(vector).extract(0, 2);
    assertEquals(expected, v1);
    assertEquals(expected, v2);
  }

  public void testStackFail() {
    GeometricLayer geometricLayer = new GeometricLayer(IdentityMatrix.of(3), Array.zeros(3));
    geometricLayer.popMatrix();
    AssertFail.of(() -> geometricLayer.popMatrix());
  }

  public void testSerializableFail() {
    GeometricLayer geometricLayer = new GeometricLayer(IdentityMatrix.of(3), Array.zeros(3));
    try {
      Serialization.copy(geometricLayer);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
