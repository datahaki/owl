// code by jph
package ch.alpine.owl.img;

enum IbericoStats {
  ;
  static void main() {
    IbericoImports ibericoImports = new IbericoImports(true);
    IbericoPlots ibericoPlots = new IbericoPlots();
    ibericoPlots.add(ibericoImports.winter.get(0).routes, "Spain 23/24");
    ibericoPlots.add(ibericoImports.winter.get(1).routes, "France 24");
    ibericoPlots.add(ibericoImports.winter.get(2).routes, "Spain 24/25");
    ibericoPlots.show();
  }
}
