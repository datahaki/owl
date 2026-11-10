// code by jph
package ch.alpine.owl.net;

import ch.alpine.bridge.pro.VoidProvider;

class SVDemo implements VoidProvider {
  final ScopedValue<Integer> sv = ScopedValue.newInstance();

  @Override
  public Void runStandalone() {
    String call1 = ScopedValue.where(sv, 123).call(() -> doSomething());
    String call2 = ScopedValue.where(sv, 234).call(() -> doSomething());
    IO.println(call1);
    IO.println(call2);
    return null;
  }

  private String doSomething() {
    return "here" + sv.get();
  }

  static void main() {
    new SVDemo().runStandalone();
  }
}
