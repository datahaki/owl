// code by jph
package ch.alpine.ubongo;

import java.util.stream.Stream;

public enum UbongoPrep {
  ;
  public static void main(String[] args) {
    Stream.of(UbongoBoards.values()).filter(ub -> ub.use() == 7).parallel().forEach(UbongoLoader.INSTANCE::load);
    // for (UbongoBoards ubongoBoards : UbongoBoards.values()) {
    // if (ubongoBoards.use() == 7) {
    // System.out.println(ubongoBoards);
    // UbongoLoader.INSTANCE.load(ubongoBoards);
    // }
    // }
  }
}
