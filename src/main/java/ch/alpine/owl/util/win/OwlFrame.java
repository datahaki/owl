// code by jph and jl
package ch.alpine.owl.util.win;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.alpine.ascona.util.win.BaseFrame;
import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.owl.data.tree.Nodes;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.util.ren.RenderElements;
import ch.alpine.owl.util.ren.TransitionRender;
import ch.alpine.sophus.crv.TransitionSpace;
import ch.alpine.tensor.ext.Serialization;

public class OwlFrame extends BaseFrame {
  private boolean replay = false;
  private int replayIndex = 0;
  private final List<TrajectoryPlanner> backup = new ArrayList<>();
  private final JSlider jSlider = new JSlider();

  public OwlFrame() {
    {
      JToggleButton jToggleButton = new JToggleButton("Replay");
      jToggleButton.setToolTipText("stops LiveFeed and goes to Replaymode");
      jToggleButton.addActionListener(actionEvent -> replay = jToggleButton.isSelected());
      jToolBar.add(jToggleButton);
    }
    {
      JButton jButton = new JButton("<<");
      jButton.setToolTipText("Replay: 1 Step back");
      jButton.addActionListener(actionEvent -> {
        if (replayIndex > 0) {
          replayIndex = replayIndex - 1;
        } else {
          replayIndex = 0;
          System.err.println("GUI: Already displaying first Planningstep");
        }
        jSlider.setValue(replayIndex);
      });
      jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton(">>");
      jButton.setToolTipText("Replay: 1 Step forward");
      jButton.addActionListener(actionEvent -> {
        if (replayIndex < backup.size() - 1) {
          replayIndex = replayIndex + 1;
        } else {
          replayIndex = backup.size() - 1;
          System.err.println("GUI: Already displaying latest Planningstep");
        }
        jSlider.setValue(replayIndex);
      });
      jToolBar.add(jButton);
    }
    {
      jSlider.setOpaque(false);
      jSlider.addChangeListener(changeEvent -> {
        replayIndex = jSlider.getValue();
        repaint(replayIndex);
      });
      jToolBar.add(jSlider);
    }
  }

  public void setGlc(TrajectoryPlanner trajectoryPlanner) {
    try {
      backup.add(Serialization.copy(trajectoryPlanner));
      jSlider.setMaximum(backup.size() - 1);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    if (!replay) { // live feed
      replayIndex = backup.size() - 1;
      jSlider.setValue(replayIndex);
    }
  }

  private void repaint(int index) {
    if (0 <= index && index < backup.size())
      try {
        geometricComponent.setRenderInterfaces( //
            RenderElements.create(backup.get(index)));
        // jStatusLabel.setText(backup.get(index).infoString());
        geometricComponent.jComponent.repaint();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  public void setRrts(TransitionSpace transitionSpace, RrtsNode root, TransitionRegionQuery transitionRegionQuery) {
    try {
      Collection<RrtsNode> nodes = Nodes.ofSubtree(root);
      Collection<RrtsNode> collection = Serialization.copy(nodes);
      geometricComponent.setRenderInterfaces( //
          RenderElements.create(collection, Serialization.copy(transitionRegionQuery)));
      geometricComponent.addRenderInterface(new TransitionRender(transitionSpace).setCollection(collection));
      geometricComponent.jComponent.repaint();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void addBackground(RenderInterface renderInterface) {
    if (Objects.nonNull(renderInterface))
      geometricComponent.addRenderInterfaceBackground(renderInterface);
  }
}
