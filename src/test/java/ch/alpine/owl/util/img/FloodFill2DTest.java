// code by jph
package ch.alpine.owl.util.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.ExactTensorQ;

class FloodFill2DTest {
  @Test
  void testSimple() {
    Tensor tensor = Array.zeros(5, 6);
    Tensor seeds = Tensors.fromString("{{2, 2}, {4, 3}}");
    int ttl = 3;
    Tensor manh = FloodFill2D.of(tensor, ttl, seeds.stream().collect(Collectors.toSet()));
    String s = "{{0, 0, 1, 0, 0, 0}, {0, 1, 2, 1, 0, 0}, {1, 2, 3, 2, 1, 0}, {0, 1, 2, 2, 1, 0}, {0, 1, 2, 3, 2, 1}}";
    assertEquals(manh, Tensors.fromString(s));
    ExactTensorQ.require(manh);
  }

  @Test
  void testObstacles() {
    Tensor tensor = Array.zeros(5, 6);
    tensor.set(Tensors.vector(0, 1, 1, 1, 1), Tensor.ALL, 1);
    Tensor seeds = Tensors.fromString("{{2, 2}, {4, 3}}");
    int ttl = 10;
    Tensor manh = FloodFill2D.of(tensor, ttl, seeds.stream().collect(Collectors.toSet()));
    String s = "{{6, 7, 8, 7, 6, 5}, {5, 0, 9, 8, 7, 6}, {4, 0, 10, 9, 8, 7}, {3, 0, 9, 9, 8, 7}, {2, 0, 9, 10, 9, 8}}";
    assertEquals(manh, Tensors.fromString(s));
    ExactTensorQ.require(manh);
  }

  @Test
  void testSeeds() {
    Tensor tensor = Tensors.matrixInt(new int[][] { //
        { 0, 0, 0, 0, 0 }, //
        { 0, 0, 0, 1, 1 }, //
        { 0, 0, 0, 0, 1 }, //
        { 1, 0, 0, 0, 0 }, //
    });
    Set<Tensor> seeds = FloodFill2D.seeds(tensor);
    assertEquals(seeds.size(), 7);
    assertTrue(seeds.contains(Tensors.vector(2, 0)));
    assertTrue(seeds.contains(Tensors.vector(0, 3)));
    assertTrue(seeds.contains(Tensors.vector(0, 4)));
    assertTrue(seeds.contains(Tensors.vector(1, 2)));
    // ---
    int ttl = 3;
    Tensor manh = FloodFill2D.of(tensor, ttl, seeds);
    String s = "{{1, 1, 2, 3, 3}, {2, 2, 3, 0, 0}, {3, 2, 2, 3, 0}, {0, 3, 2, 2, 3}}";
    assertEquals(manh, Tensors.fromString(s));
    ExactTensorQ.require(manh);
  }

  @Test
  void testInvalid() {
    Tensor tensor = Array.zeros(5, 6);
    int ttl = -1;
    assertThrows(Exception.class, () -> FloodFill2D.of(tensor, ttl));
  }
}
