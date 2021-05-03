package ch.alpine.sophus.app.decim;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.sophus.hs.sn.SnManifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.IdentityMatrix;

public enum SnTransportChain {
  ;
  public static List<Tensor> endos(Tensor sequence) {
    int m = Unprotect.dimension1Hint(sequence);
    Tensor x = IdentityMatrix.of(m);
    List<Tensor> list = new ArrayList<>(sequence.length());
    list.add(x);
    for (int index = 1; index < sequence.length(); ++index) {
      Tensor p = sequence.get(index - 1);
      Tensor q = sequence.get(index);
      Tensor f = SnManifold.INSTANCE.endomorphism(q, p);
      x = x.dot(f);
      list.add(x);
    }
    return list;
  }
}
