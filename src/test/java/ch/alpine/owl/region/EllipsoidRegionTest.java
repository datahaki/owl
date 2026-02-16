// code by jph
package ch.alpine.owl.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TimeDependentRegion;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophis.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.ext.Serialization;

class EllipsoidRegionTest {
  @Test
  void testDisjoint() {
    TrajectoryRegionQuery goalQuery = //
        CatchyTrajectoryRegionQuery.timeInvariant( //
            new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(1, 1)));
    List<StateTime> trajectory = new ArrayList<>();
    trajectory.add(new StateTime(Tensors.vector(0, 5), RealScalar.ZERO));
    trajectory.add(new StateTime(Tensors.vector(5, 5), RealScalar.ZERO));
    assertFalse(goalQuery.firstMember(trajectory).isPresent());
    // ---
    StateTime term = new StateTime(Tensors.vector(10, 5), RealScalar.ZERO);
    trajectory.add(term);
    assertTrue(goalQuery.firstMember(trajectory).isPresent());
    assertEquals(goalQuery.firstMember(trajectory).get(), term);
    assertFalse(!goalQuery.firstMember(trajectory).isPresent());
  }

  @Test
  void testSimple23() {
    Region<StateTime> region = //
        new TimeDependentRegion(new EllipsoidRegion(Tensors.vector(1, 2), Tensors.vector(3, 4)));
    assertTrue(region.test(new StateTime(Tensors.vector(1), RealScalar.of(2))));
    assertFalse(region.test(new StateTime(Tensors.vector(1), RealScalar.of(7))));
  }

  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    MemberQ region = Serialization.copy(new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(1, 1)));
    assertTrue(region.test(Tensors.vector(10, 5)));
    assertTrue(region.test(Tensors.vector(10, 5.5)));
    assertTrue(region.test(Tensors.vector(10, 6)));
    assertFalse(region.test(Tensors.vector(10, 6.5)));
  }

  @Test
  void testSimple2() {
    MemberQ region = new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(2, 2));
    assertTrue(region.test(Tensors.vector(10, 5)));
    assertTrue(region.test(Tensors.vector(10, 5.5)));
    assertTrue(region.test(Tensors.vector(10, 7)));
    assertTrue(region.test(Tensors.vector(12, 5)));
    assertTrue(region.test(Tensors.vector(11.2, 6.2)));
    assertFalse(region.test(Tensors.vector(10, 7.1)));
    assertFalse(region.test(Tensors.vector(10, 7.5)));
  }

  @Test
  void testEllipsoid() {
    MemberQ region = new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(2, 1));
    assertTrue(region.test(Tensors.vector(10, 5)));
    assertTrue(region.test(Tensors.vector(10, 5.5)));
    assertFalse(region.test(Tensors.vector(10, 7)));
    assertTrue(region.test(Tensors.vector(12, 5)));
    assertFalse(region.test(Tensors.vector(12.1, 5)));
    assertFalse(region.test(Tensors.vector(11.2, 6.2)));
    assertFalse(region.test(Tensors.vector(10, 6.1)));
    assertFalse(region.test(Tensors.vector(10, 7.5)));
  }

  @Test
  void testInfty() {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(5, 10), Tensors.vector(1 / 0.0, 2));
    assertEquals(ifr.signedDistance(Tensors.vector(1000, 8)), RealScalar.ZERO);
  }

  @Test
  void test1D() {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(10), Tensors.vector(2));
    assertEquals(ifr.signedDistance(Tensors.vector(8)), RealScalar.ZERO);
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(10), Tensors.vector(2));
    Serialization.copy(ifr);
  }

  @Test
  void testLengthFail() {
    assertThrows(Exception.class, () -> new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, 0, 3)));
  }

  @Test
  void testNegativeFail() {
    assertThrows(Exception.class, () -> new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, -2)));
  }

  @Test
  void testZeroFail() {
    assertThrows(Exception.class, () -> new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, 0)));
    assertThrows(Exception.class, () -> new EllipsoidRegion(Tensors.vector(10, 2, 3), Tensors.vector(1, 0.0, 3)));
  }
}
