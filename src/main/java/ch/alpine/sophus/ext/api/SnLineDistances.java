// code by jph
package ch.alpine.sophus.ext.api;

import ch.alpine.sophus.decim.HsLineDistance;
import ch.alpine.sophus.decim.LineDistance;
import ch.alpine.sophus.decim.SymmetricLineDistance;
import ch.alpine.sophus.hs.sn.SnLineDistance;
import ch.alpine.sophus.hs.sn.SnLineDistanceAlt;
import ch.alpine.sophus.hs.sn.SnManifold;

public enum SnLineDistances {
  DEFAULT(SnLineDistance.INSTANCE), //
  DEF_ALT(SnLineDistanceAlt.INSTANCE), //
  PROJECT(new HsLineDistance(SnManifold.INSTANCE)), //
  SYMMETR(new SymmetricLineDistance(new HsLineDistance(SnManifold.INSTANCE))), //
  ;

  private final LineDistance lineDistance;

  private SnLineDistances(LineDistance lineDistance) {
    this.lineDistance = lineDistance;
  }

  public LineDistance lineDistance() {
    return lineDistance;
  }
}
