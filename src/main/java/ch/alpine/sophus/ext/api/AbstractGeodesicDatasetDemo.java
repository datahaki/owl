// code by jph
package ch.alpine.sophus.ext.api;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.sophus.demo.io.GokartPoseData;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;

// TODO OWL the contents of this package need to be restructured
@ReflectionMarker
public abstract class AbstractGeodesicDatasetDemo extends AbstractGeodesicDisplayDemo {
  protected final GokartPoseData gokartPoseData;
  protected final SpinnerLabel<String> spinnerLabelString = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerLabelLimit = new SpinnerLabel<>();

  public AbstractGeodesicDatasetDemo(List<ManifoldDisplay> list, GokartPoseData gokartPoseData) {
    super(list);
    this.gokartPoseData = gokartPoseData;
    {
      spinnerLabelString.setList(gokartPoseData.list());
      spinnerLabelString.addSpinnerListener(type -> updateState());
      spinnerLabelString.setIndex(0);
      spinnerLabelString.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "data");
    }
    {
      spinnerLabelLimit.setList(Arrays.asList(500, 750, 800, 900, 1000, 1500, 2000, 3000, 5000));
      spinnerLabelLimit.setIndex(1);
      spinnerLabelLimit.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "limit");
      spinnerLabelLimit.addSpinnerListener(type -> updateState());
    }
    timerFrame.jToolBar.addSeparator();
  }

  protected abstract void updateState();
}
