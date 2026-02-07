// code by jph
package ch.alpine.ascona.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.qhull3d.ConvexHull3D;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class ConvexHull3DTest {
  @Test
  void testSimple() {
    CoordinateBoundingBox ccb = CoordinateBoundingBox.of(Clips.unit(), Clips.unit(), Clips.interval(-3, 3));
    Tensor tensor = RandomSample.of(new BoxRandomSample(ccb), 100);
    List<int[]> list1 = ConvexHull3D.of(tensor);
    List<int[]> list2 = ConvexHull3D.of(tensor.map(s -> Quantity.of(s, "m")));
    assertEquals(list1.size(), list2.size());
    for (int index = 0; index < list1.size(); ++index)
      assertEquals(Integers.asList(list1.get(index)), Integers.asList(list2.get(index)));
  }

  @Test
  void testCuboid() {
    Clip[] clips = { Clips.unit(), Clips.unit(), Clips.interval(-3, 3) };
    CoordinateBoundingBox ccb = CoordinateBoundingBox.of(clips);
    RandomGenerator randomGenerator = ThreadLocalRandom.current();
    Tensor tensor = RandomSample.of(new BoxRandomSample(ccb), randomGenerator, 200);
    for (int index = 0; index < tensor.length(); ++index) {
      int i = randomGenerator.nextInt(3);
      if (randomGenerator.nextBoolean())
        tensor.set(clips[i].min(), index, i);
      else
        tensor.set(clips[i].max(), index, i);
    }
    List<int[]> index = ConvexHull3D.of(tensor);
    long count = index.stream().filter(a -> a.length > 3).count();
    assertTrue(5 <= count); // typically count is 6
    assertTrue(0 <= index.stream().flatMapToInt(IntStream::of).min().orElse(-1));
  }
}
