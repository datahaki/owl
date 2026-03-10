// code by jph
package ch.alpine.owl;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import ch.alpine.ascony.win.TimerFrame;
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
    TimerFrame timerFrame = demoInterface.getWindow();
    timerFrame.setTitle(demoInterface.getClass().getSimpleName());
    timerFrame.setVisible(true);
    try {
      Thread.sleep(400);
    } catch (Exception exception) {
      // ---
    }
    timerFrame.setVisible(false);
    timerFrame.dispose();
  }
}
