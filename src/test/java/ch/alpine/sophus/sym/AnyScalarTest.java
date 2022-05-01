// code by jph
package ch.alpine.sophus.sym;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.tri.ArcTan;

class AnyScalarTest {
  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(AnyScalar.INSTANCE);
  }

  @Test
  public void testMultiply() {
    assertEquals(AnyScalar.INSTANCE.multiply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(RealScalar.ZERO.multiply(AnyScalar.INSTANCE), RealScalar.ZERO);
    assertEquals(Pi.HALF.multiply(AnyScalar.INSTANCE).toString(), AnyScalar.INSTANCE.toString());
    assertEquals(AnyScalar.INSTANCE.multiply(AnyScalar.INSTANCE).toString(), AnyScalar.INSTANCE.toString());
  }

  @Test
  public void testArg() {
    assertEquals(Arg.FUNCTION.apply(AnyScalar.INSTANCE).toString(), AnyScalar.INSTANCE.toString());
  }

  @Test
  public void testExp() {
    assertEquals(Exp.FUNCTION.apply(AnyScalar.INSTANCE).toString(), AnyScalar.INSTANCE.toString());
    assertEquals(Log.FUNCTION.apply(AnyScalar.INSTANCE).toString(), AnyScalar.INSTANCE.toString());
  }

  @Test
  public void testArcTan() {
    // ArcTan.FUNCTION.apply(AnyScalar.INSTANCE);
    assertEquals(ArcTan.of(RealScalar.ONE, AnyScalar.INSTANCE).toString(), AnyScalar.INSTANCE.toString());
    // assertEquals(ArcTan.of(AnyScalar.INSTANCE, RealScalar.ONE).toString(), AnyScalar.INSTANCE.toString());
  }
}
