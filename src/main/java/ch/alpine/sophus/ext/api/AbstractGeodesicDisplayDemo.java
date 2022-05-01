// code by jph
package ch.alpine.sophus.ext.api;

import java.awt.Dimension;
import java.util.List;

import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.java.win.AbstractDemo;
import ch.alpine.java.win.BaseFrame;
import ch.alpine.java.win.DemoInterface;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.javax.swing.SpinnerListener;
import ch.alpine.sophus.ext.dis.GeodesicDisplayRender;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;

@ReflectionMarker
public abstract class AbstractGeodesicDisplayDemo extends AbstractDemo implements DemoInterface {
  private final SpinnerLabel<ManifoldDisplay> manifoldDisplaySpinner = new SpinnerLabel<>();
  private final List<ManifoldDisplay> list;

  public AbstractGeodesicDisplayDemo(List<ManifoldDisplay> list) {
    if (list.isEmpty())
      throw new RuntimeException();
    this.list = list;
    manifoldDisplaySpinner.setList(list);
    manifoldDisplaySpinner.setValue(list.get(0));
    if (1 < list.size()) {
      manifoldDisplaySpinner.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "geodesic type");
      timerFrame.jToolBar.addSeparator();
    }
    timerFrame.geometricComponent.addRenderInterfaceBackground(new GeodesicDisplayRender() {
      @Override
      public ManifoldDisplay getGeodesicDisplay() {
        return manifoldDisplay();
      }
    });
  }

  /** @return */
  public final ManifoldDisplay manifoldDisplay() {
    return manifoldDisplaySpinner.getValue();
  }

  public synchronized final void setGeodesicDisplay(ManifoldDisplay geodesicDisplay) {
    manifoldDisplaySpinner.setValue(geodesicDisplay);
  }

  public void addSpinnerListener(SpinnerListener<ManifoldDisplay> spinnerListener) {
    manifoldDisplaySpinner.addSpinnerListener(spinnerListener);
  }

  /** @return */
  public List<ManifoldDisplay> getManifoldDisplays() {
    return list;
  }

  @Override // from DemoInterface
  public final BaseFrame start() {
    return timerFrame;
  }
}
