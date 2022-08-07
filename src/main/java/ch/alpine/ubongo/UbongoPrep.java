// code by jph
package ch.alpine.ubongo;

import java.util.stream.Stream;

public enum UbongoPrep {
  ;
  public static void main(String[] args) {
    Stream.of(UbongoBoards.values()) //
        .filter(ub -> ub.use() > 7) //
        .parallel() //
        .forEach(UbongoLoader.INSTANCE::load);
  }
}
