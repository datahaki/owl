// code by jph
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

public class AbstractEboTrackerTest {
  @Test
  public void testPermutations() {
    Tensor slackVector = Tensors.vector(1, 2, 0.5);
    Distribution distribution = UniformDistribution.of(0, 3);
    Tensor values = RandomVariate.of(distribution, 300, 3);
    final Set<Integer> minKeys1;
    {
      AbstractEboTracker<Integer> lexicographicSemiorderMinTracker = //
          (AbstractEboTracker<Integer>) SetEboTracker.<Integer>withList(slackVector);
      for (int index = 0; index < values.length(); ++index)
        lexicographicSemiorderMinTracker.digest(index, values.get(index));
      minKeys1 = new HashSet<>(lexicographicSemiorderMinTracker.getMinKeys());
    }
    List<Integer> list = IntStream.range(0, values.length()).boxed().collect(Collectors.toList());
    for (int round = 0; round < 10; ++round) {
      Collections.shuffle(list);
      SetEboTracker<Integer> lsmtc = //
          (SetEboTracker<Integer>) SetEboTracker.<Integer>withList(slackVector);
      for (int index = 0; index < values.length(); ++index) {
        int count = list.get(index);
        lsmtc.digest(count, values.get(count));
      }
      Collection<Integer> minKeys2 = lsmtc.getMinKeys();
      assertEquals(minKeys1, new HashSet<>(minKeys2));
    }
  }

  @Test
  public void testEmptyPollFail() {
    Tensor slackVector = Tensors.vector(1, 2, 0.5);
    EboTracker<Integer> lexicographicSemiorderMinTracker = //
        SetEboTracker.<Integer>withList(slackVector);
    AssertFail.of(() -> lexicographicSemiorderMinTracker.pollBestKey());
  }

  @Test
  public void testMatrixSlackFail() {
    AssertFail.of(() -> SetEboTracker.withList(HilbertMatrix.of(3)));
  }

  @Test
  public void testScalarSlackFail() {
    AssertFail.of(() -> SetEboTracker.withList(Pi.VALUE));
  }
}
