// code by jph
package ch.alpine.owl;

import ch.alpine.bridge.io.FileBlock;
import ch.alpine.bridge.io.ResourceLocator;
import ch.alpine.bridge.pro.RunLaunchPad;

enum LocalLaunchPad {
  ;
  static void main() throws Exception {
    if (!FileBlock.of(ResourceLocator.of(LocalLaunchPad.class).resolve("")))
      RunLaunchPad.create(LocalLaunchPad.class.getPackageName()).runStandalone();
  }
}
