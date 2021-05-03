// code by jph
package ch.alpine.sophus.io.obj;

import java.util.StringTokenizer;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ enum StaticHelper {
  ;
  static Tensor three(String string) {
    StringTokenizer stringTokenizer = new StringTokenizer(string);
    return Tensors.of( //
        Scalars.fromString(stringTokenizer.nextToken()), //
        Scalars.fromString(stringTokenizer.nextToken()), //
        Scalars.fromString(stringTokenizer.nextToken()));
  }

  static String[] slash(String string) {
    int i0 = string.indexOf('/');
    int b1 = i0 + 1;
    int i1 = string.indexOf('/', b1);
    return new String[] { //
        string.substring(0, i0), //
        string.substring(b1, i1), //
        string.substring(i1 + 1, string.length()) };
  }
}
