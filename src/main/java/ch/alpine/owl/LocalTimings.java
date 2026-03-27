// code by jph
package ch.alpine.owl;

import ch.alpine.bridge.pro.RunProviderTimings;

enum LocalTimings {
  ;
  static void main() throws Exception {
    RunProviderTimings.of(LocalTimings.class.getPackageName());
  }
}
