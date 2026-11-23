// code by jph
package ch.alpine.owl;

import ch.alpine.bridge.pro.RunLaunchPad;

enum LocalLaunchPad {
  ;
  static void main() throws Exception {
    RunLaunchPad.create(LocalLaunchPad.class.getPackageName()).runStandalone();
  }
}
