// code by jph
package ch.alpine.owl;

import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import ch.alpine.bridge.cgr.InstanceDiscovery;
import ch.alpine.bridge.lang.FriendlyFormat;
import ch.alpine.bridge.pro.WindowProvider;
import ch.alpine.owl.util.win.DemoInterface;

/** scans repository for classes that implement {@link DemoInterface}
 * DemoLauncher creates a gui that allows to start these classes. */
public enum DemoLauncher implements WindowProvider {
  INSTANCE;

  @Override
  public Window getWindow() {
    JFrame jFrame = new JFrame();
    List<Supplier<DemoInterface>> list = //
        InstanceDiscovery.of(getClass().getPackageName(), DemoInterface.class);
    JPanel jPanel = new JPanel(new GridLayout(list.size(), 1));
    for (Supplier<DemoInterface> supplier : list) {
      DemoInterface showProvider = supplier.get();
      JButton jButton = new JButton(FriendlyFormat.defaultTitle(showProvider.getClass()));
      jButton.addActionListener(_ -> supplier.get().runStandalone());
      jPanel.add(jButton);
    }
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(new JScrollPane(jPanel));
    return jFrame;
  }

  static void main() {
    INSTANCE.runStandalone();
  }
}
