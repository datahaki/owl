// code by jph
package ch.alpine.owl;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import ch.alpine.ascony.win.BaseFrame;
import ch.alpine.bridge.cgr.InstanceDiscovery;
import ch.alpine.owl.util.win.DemoInterface;

class DemoInterfaceTest implements Consumer<DemoInterface> {
  @TestFactory
  Stream<DynamicTest> dynamicTests() {
    return InstanceDiscovery.of(getClass().getPackageName(), DemoInterface.class).stream() //
        .map(instanceRecord -> DynamicTest.dynamicTest(instanceRecord.toString(), //
            () -> accept(instanceRecord.supplier().get())));
  }

  @Override
  public void accept(DemoInterface demoInterface) {
    BaseFrame baseFrame = demoInterface.getBaseFrame();
    baseFrame.jFrame.setTitle(demoInterface.getClass().getSimpleName());
    baseFrame.jFrame.setVisible(true);
    try {
      Thread.sleep(400);
    } catch (Exception exception) {
      // ---
    }
    baseFrame.jFrame.setVisible(false);
    baseFrame.jFrame.dispose();
  }
}
