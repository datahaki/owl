// code by jph
package ch.alpine.owl.bot.rn.rrts;

import java.util.Collection;

import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.core.DefaultRrts;
import ch.alpine.owl.rrts.core.Rrts;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsNodeTransition;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import junit.framework.TestCase;

public class RnRrtsNodeCollectionTest extends TestCase {
  private static final TransitionSpace TRANSITION_SPACE = RnTransitionSpace.INSTANCE;

  public void testSimple() {
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(10, 10)));
    TransitionRegionQuery transitionRegionQuery = EmptyTransitionRegionQuery.INSTANCE;
    Rrts rrts = new DefaultRrts(TRANSITION_SPACE, rrtsNodeCollection, transitionRegionQuery, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 0).get();
    assertEquals(root.children().size(), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0), 0).get();
    assertEquals(root.children().size(), 1);
    assertEquals(n1.costFromRoot(), RealScalar.of(1));
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(1, 1), 0).get();
    assertEquals(root.children().size(), 1);
    assertEquals(n1.children().size(), 1);
    assertEquals(n1.children().iterator().next(), n2);
    assertEquals(n1.costFromRoot(), RealScalar.of(1));
    assertEquals(n2.children().size(), 0);
    assertEquals(n2.costFromRoot(), RealScalar.of(2));
  }

  public void testQuantity() {
    CoordinateBoundingBox box = CoordinateBounds.of(Tensors.fromString("{-5[m], -7[m]}"), Tensors.fromString("{10[m], 10[m]}"));
    RrtsNodeCollection rrtsNodeCollection = new RnRrtsNodeCollection(box);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(box);
    for (int count = 0; count < 30; ++count) {
      Tensor tensor = RandomSample.of(randomSampleInterface);
      rrtsNodeCollection.insert(RrtsNode.createRoot(tensor, RealScalar.ONE));
    }
    Collection<RrtsNodeTransition> collection = rrtsNodeCollection.nearTo(Tensors.fromString("{2[m], 3[m]}"), 10);
    assertEquals(collection.size(), 10);
  }
}
