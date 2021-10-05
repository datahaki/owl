// code by gjoel
package ch.alpine.java.win;

import java.awt.Window;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.synth.SynthLookAndFeel;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

public enum LookAndFeels {
  /** java swing default */
  DEFAULT(new MetalLookAndFeel()), //
  DARK(new FlatDarkLaf()), //
  LIGHT(new FlatLightLaf()), //
  DRACULA(new FlatDarculaLaf()), //
  INTELLI_J(new FlatIntelliJLaf()), //
  /** synth gives trouble on linux: dash pc, jan's pc ... */
  SYNTH(new SynthLookAndFeel()), //
  NIMBUS(new NimbusLookAndFeel());

  private final LookAndFeel lookAndFeel;

  private LookAndFeels(LookAndFeel lookAndFeel) {
    this.lookAndFeel = lookAndFeel;
  }

  public LookAndFeel get() {
    return lookAndFeel;
  }

  public void updateUI() {
    try {
      UIManager.setLookAndFeel(lookAndFeel);
      for (Window window : Window.getWindows())
        SwingUtilities.updateComponentTreeUI(window);
    } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
      unsupportedLookAndFeelException.printStackTrace();
    }
  }
}