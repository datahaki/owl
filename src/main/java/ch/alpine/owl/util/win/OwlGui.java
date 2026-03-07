// code by jph
package ch.alpine.owl.util.win;

import java.util.Collection;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.util.ren.RenderElements;
import ch.alpine.owl.util.ren.TransitionRender;
import ch.alpine.owlets.data.tree.Nodes;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.rrts.core.RrtsNode;
import ch.alpine.owlets.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.ts.TransitionSpace;

public enum OwlGui {
  ;
  public static TimerFrame glc(TrajectoryPlanner trajectoryPlanner) {
    TimerFrame owlFrame = new TimerFrame();
    RenderElements.create(trajectoryPlanner) //
        .forEach(owlFrame.geometricComponent::addRenderInterface);
    owlFrame.jFrame.setBounds(100, 100, 800, 800);
    owlFrame.jFrame.setVisible(true);
    return owlFrame;
  }

  public static TimerFrame rrts(TransitionSpace transitionSpace, RrtsNode root, TransitionRegionQuery transitionRegionQuery) {
    TimerFrame owlFrame = new TimerFrame();
    Collection<RrtsNode> nodes = Nodes.ofSubtree(root);
    Collection<RrtsNode> collection = nodes;
    RenderElements.create(collection, transitionRegionQuery) //
        .forEach(owlFrame.geometricComponent::addRenderInterface);
    owlFrame.geometricComponent.addRenderInterface(new TransitionRender(transitionSpace).setCollection(collection));
    owlFrame.jFrame.setBounds(100, 100, 800, 800);
    owlFrame.jFrame.setVisible(true);
    return owlFrame;
  }
}
