// code by jph
package ch.alpine.owl.util.win;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import ch.alpine.owl.util.bot.DemoInterfaceHelper;
import ch.alpine.tensor.ext.ref.InstanceDiscovery;

class DemoInterfaceTest implements Consumer<DemoInterface> {
  private static final AtomicInteger COUNT = new AtomicInteger();

  @TestFactory
  Stream<DynamicTest> dynamicTests() {
    return InstanceDiscovery.of("ch.alpine", DemoInterface.class).stream() //
        .map(Supplier::get) //
        .map(instance -> DynamicTest.dynamicTest(instance.toString(), () -> accept(instance)));
  }

  @Override
  public void accept(DemoInterface demoInterface) {
    DemoInterfaceHelper.brief(demoInterface);
  }

  @AfterAll
  static void here() {
    assertTrue(13 <= COUNT.get());
  }
}
