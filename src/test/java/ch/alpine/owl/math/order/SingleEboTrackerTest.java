// code by astoll, jph
package ch.alpine.owl.math.order;

import java.util.Collection;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class SingleEboTrackerTest extends TestCase {
  private static void _checkSimple(AbstractEboTracker<Integer> LSMT1) {
    Tensor x = Tensors.fromString("{1, 2, 2}");
    LSMT1.digest(1, x);
    assertFalse(LSMT1.getCandidateSet().isEmpty());
  }

  public void testDigestSimple() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    _checkSimple((AbstractEboTracker<Integer>) SingleEboTracker.<Integer>withList(slacks));
    _checkSimple((AbstractEboTracker<Integer>) SingleEboTracker.<Integer>withSet(slacks));
  }

  /***************************************************/
  private static void _checkDigest(AbstractEboTracker<Integer> LSMT1) {
    Tensor x = Tensors.fromString("{1}");
    Tensor y = Tensors.fromString("{3.5}");
    Tensor z = Tensors.fromString("{1.5}");
    Tensor w = Tensors.fromString("{-1.5}");
    assertTrue(LSMT1.getCandidateSet().isEmpty());
    assertTrue(LSMT1.digest(1, x).isEmpty());
    assertTrue(LSMT1.getCandidateSet().size() == 1);
    assertTrue(LSMT1.digest(2, y).contains(2));
    assertTrue(LSMT1.digest(3, z).contains(3));
    assertTrue(LSMT1.digest(4, w).contains(1));
    assertTrue(LSMT1.digest(5, w).contains(5));
  }

  public void testDigest() {
    Tensor slacks = Tensors.fromString("{2}");
    _checkDigest((AbstractEboTracker<Integer>) SingleEboTracker.<Integer>withList(slacks));
    _checkDigest((AbstractEboTracker<Integer>) SingleEboTracker.<Integer>withSet(slacks));
  }

  /***************************************************/
  private static void _checkCS(AbstractEboTracker<Integer> lexSemiMinTracker) {
    Tensor x = Tensors.fromString("{1, 4, 4}");
    Tensor y = Tensors.fromString("{3, 3, 1}");
    Tensor z = Tensors.fromString("{1.5, 4, 4}");
    Tensor w = Tensors.fromString("{0.9, 2.9, 1}");
    assertTrue(lexSemiMinTracker.getCandidateSet().isEmpty());
    {
      Collection<Integer> collection = lexSemiMinTracker.digest(1, x);
      assertTrue(collection.isEmpty());
    }
    assertTrue(lexSemiMinTracker.getCandidateSet().size() == 1);
    assertTrue(lexSemiMinTracker.getCandidateKeys().contains(1));
    assertTrue(lexSemiMinTracker.getCandidateValues().contains(x));
    {
      Collection<Integer> collection = lexSemiMinTracker.digest(2, y);
      assertTrue(collection.isEmpty());
    }
    assertTrue(lexSemiMinTracker.getCandidateSet().size() == 2);
    assertTrue(lexSemiMinTracker.getCandidateKeys().contains(2));
    assertTrue(lexSemiMinTracker.getCandidateValues().contains(y));
    {
      Collection<Integer> collection = lexSemiMinTracker.digest(3, z);
      assertTrue(collection.contains(3));
      assertEquals(collection.size(), 1);
    }
    assertTrue(lexSemiMinTracker.getCandidateSet().size() == 2);
    assertTrue(!lexSemiMinTracker.getCandidateKeys().contains(3));
    assertTrue(lexSemiMinTracker.getCandidateValues().contains(x) && lexSemiMinTracker.getCandidateValues().contains(y)
        && !lexSemiMinTracker.getCandidateValues().contains(z));
    {
      Collection<Integer> collection = lexSemiMinTracker.digest(4, w);
      assertTrue(collection.contains(1));
      assertTrue(collection.contains(2));
      assertEquals(collection.size(), 2);
    }
    assertTrue(lexSemiMinTracker.getCandidateSet().size() == 1);
    assertTrue(lexSemiMinTracker.getCandidateKeys().contains(4));
    assertFalse(lexSemiMinTracker.getCandidateKeys().contains(1) && lexSemiMinTracker.getCandidateKeys().contains(1)
        && lexSemiMinTracker.getCandidateKeys().contains(3));
  }

  public void testCandidateSet() {
    Tensor slacks = Tensors.vector(2, 2, 2);
    _checkCS((AbstractEboTracker<Integer>) SingleEboTracker.<Integer>withList(slacks));
    _checkCS((AbstractEboTracker<Integer>) SingleEboTracker.<Integer>withSet(slacks));
  }

  public void testFailNull() {
    AssertFail.of(() -> SingleEboTracker.withList(null));
    AssertFail.of(() -> SingleEboTracker.withSet(null));
  }
}
