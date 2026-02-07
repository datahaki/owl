// code by astoll
package ch.alpine.owl.order;

enum JassType {
  SIX(1),
  SEVEN(2),
  EIGHT(3),
  NINE(8),
  TEN(4),
  JACK(9),
  QUEEN(5),
  KING(6),
  ACE(7);

  final int trumpfOrdering;

  private JassType(int trumpfOrdering) {
    this.trumpfOrdering = trumpfOrdering;
  }
}
