// code by jph
package ch.alpine.ascona.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.qhull3d.PlatonicSolid;
import ch.alpine.sophis.srf.SimplexD;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.RotateLeft;

class PlatonicSolidTest {
  @ParameterizedTest
  @EnumSource
  void testBorder(PlatonicSolid platonicSolid) {
    Set<Tensor> border = SimplexD.of(platonicSolid.faces());
    assertTrue(border.isEmpty());
  }

  @ParameterizedTest
  @EnumSource
  void test(PlatonicSolid platonicSolid) {
    List<int[]> faces = new ArrayList<>(platonicSolid.faces());
    Collections.shuffle(faces);
    Set<Tensor> set = SimplexD.of(faces.subList(0, faces.size() - 1));
    assertEquals(set.size(), platonicSolid.faceShape());
    int[] last = faces.getLast();
    Tensor v = Tensors.vectorInt(last);
    for (int i = 0; i < v.length(); ++i)
      assertTrue(set.remove(RotateLeft.of(v, i).extract(0, 2)));
    assertTrue(set.isEmpty());
  }
}
