// code by jph
package ch.alpine.owl;

import ch.alpine.bridge.io.FileBlock;
import ch.alpine.bridge.io.ResourceLocator;
import ch.alpine.bridge.pro.RunLaunchPad;

enum OwlLaunchPad {
  ;
  static void main() {
    if (!FileBlock.of(ResourceLocator.of(OwlLaunchPad.class).resolve("")))
      RunLaunchPad.create(OwlLaunchPad.class.getPackageName()).runStandalone();
  }
}
