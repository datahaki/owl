package ch.alpine.owl.region;

import java.util.Collection;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.MemberQ;

public record UnionMemberQ(Collection<MemberQ> collection) implements MemberQ {
  @Override
  public boolean test(Tensor t) {
    return collection.stream().anyMatch(memberQ -> memberQ.test(t));
  }
}
