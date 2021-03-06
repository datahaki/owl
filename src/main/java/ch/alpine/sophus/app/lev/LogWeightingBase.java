// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.sophus.opt.LogWeighting;
import ch.alpine.sophus.opt.LogWeightings;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;

public abstract class LogWeightingBase extends AbstractPlaceDemo {
  protected final SpinnerLabel<LogWeighting> spinnerLogWeighting = new SpinnerLabel<>();

  public LogWeightingBase(boolean addRemoveControlPoints, List<ManifoldDisplay> list, List<LogWeighting> array) {
    super(addRemoveControlPoints, list);
    {
      spinnerLogWeighting.setList(array);
      if (array.contains(LogWeightings.COORDINATE))
        spinnerLogWeighting.setValue(LogWeightings.COORDINATE);
      else
        spinnerLogWeighting.setIndex(0);
      if (1 < array.size())
        spinnerLogWeighting.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "weights");
    }
    timerFrame.jToolBar.addSeparator();
  }

  public final void addMouseRecomputation() {
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()) {
        case MouseEvent.BUTTON1: // insert point
          if (!isPositioningOngoing())
            recompute();
          break;
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        if (isPositioningOngoing())
          recompute();
      }
    };
    // ---
    timerFrame.geometricComponent.jComponent.addMouseListener(mouseAdapter);
    timerFrame.geometricComponent.jComponent.addMouseMotionListener(mouseAdapter);
  }

  /** Hint: override is possible for customization
   * 
   * @return */
  protected LogWeighting logWeighting() {
    return spinnerLogWeighting.getValue();
  }

  protected final void setLogWeighting(LogWeighting logWeighting) {
    spinnerLogWeighting.setValue(logWeighting);
    spinnerLogWeighting.reportToAll();
  }

  protected abstract TensorUnaryOperator operator(VectorLogManifold vectorLogManifold, Tensor sequence);

  protected final TensorUnaryOperator operator(Tensor sequence) {
    return operator(manifoldDisplay().hsManifold(), sequence);
  }

  protected abstract TensorScalarFunction function(Tensor sequence, Tensor values);

  protected abstract void recompute();
}
