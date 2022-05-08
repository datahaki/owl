// code by jph
package ch.alpine.owl.util.bot;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import ch.alpine.ascona.util.win.BaseFrame;
import ch.alpine.bridge.lang.ClassDiscovery;
import ch.alpine.bridge.lang.ClassPaths;
import ch.alpine.bridge.lang.ClassVisitor;
import ch.alpine.owl.util.win.DemoInterface;

/** scans repository for classes that implement {@link DemoInterface}
 * DemoLauncher creates a gui that allows to start these classes. */
public enum DemoLauncher {
  ;
  private static final int BUTTON_HEIGHT = 24;
  private static final Comparator<Class<?>> CLASSNAMECOMPARATOR = new Comparator<>() {
    @Override
    public int compare(Class<?> c1, Class<?> c2) {
      return c1.getName().compareToIgnoreCase(c2.getName());
    }
  };

  public static List<Class<?>> detect() {
    List<Class<?>> demos = new ArrayList<>();
    ClassVisitor classVisitor = new ClassVisitor() {
      @Override
      public void accept(String jarfile, Class<?> cls) {
        if (DemoInterface.class.isAssignableFrom(cls)) {
          int modifiers = cls.getModifiers();
          if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers))
            demos.add(cls);
        }
      }
    };
    ClassDiscovery.execute(ClassPaths.getDefault(), classVisitor);
    Collections.sort(demos, CLASSNAMECOMPARATOR);
    return demos;
  }

  public static void build(List<Class<?>> demos) {
    JFrame jFrame = new JFrame();
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JPanel jComponent = new JPanel(new BorderLayout());
    {
      JPanel jPanel = new JPanel(new GridLayout(demos.size(), 1));
      for (Class<?> cls : demos) {
        JButton jButton = new JButton(cls.getSimpleName());
        jButton.addActionListener(event -> {
          try {
            DemoInterface demoInterface = (DemoInterface) cls.getDeclaredConstructor().newInstance();
            BaseFrame baseFrame = demoInterface.start();
            baseFrame.jFrame.setTitle(demoInterface.getClass().getSimpleName());
            baseFrame.jFrame.setBounds(100, 100, 1200, 800);
            baseFrame.jFrame.setVisible(true);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        });
        jPanel.add(jButton);
      }
      jComponent.add(jPanel, BorderLayout.NORTH);
    }
    jFrame.setContentPane(new JScrollPane(jComponent, //
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, //
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
    jFrame.setBounds(1200, 100, 250, Math.min(40 + demos.size() * BUTTON_HEIGHT, 800));
    jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    build(detect());
  }
}
