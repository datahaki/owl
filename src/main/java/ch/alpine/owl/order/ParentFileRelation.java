// code from http://www.java2s.com/Tutorial/Java/0180__File/Checkswhetherthechilddirectoryisasubdirectoryofthebasedirectory.htm
// adapted by jph
package ch.alpine.owl.order;

import java.io.File;

import ch.alpine.bridge.io.ParentFileQ;
import ch.alpine.owl.math.order.BinaryRelation;

public enum ParentFileRelation implements BinaryRelation<File> {
  INSTANCE;

  /** Checks, whether the child directory is a subdirectory of the base
   * directory.
   *
   * @param base the base directory.
   * @param child the suspected child directory.
   * @return true, if the child is a subdirectory of the base directory.
   * @throws Exception if an error occurred during the test. */
  @Override
  public boolean test(File base, File child) {
    return ParentFileQ.test(base, child);
  }
}
