package ch.alpine.owl.region;

import java.util.Collection;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.MemberQ;

public record CapMemberQ(Collection<MemberQ> collection) implements MemberQ {
  @Override
  public boolean test(Tensor t) {
    return collection.stream().allMatch(memberQ -> memberQ.test(t));
  }
}
