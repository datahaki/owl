// code by jph
package ch.alpine.owl.ani.api;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.alpine.java.win.GeometricComponent;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.glc.adapter.EntityGlcPlannerCallback;
import ch.alpine.owl.glc.adapter.GoalConsumer;
import ch.alpine.owl.glc.adapter.SimpleGoalConsumer;
import ch.alpine.owl.glc.core.PlannerConstraint;

public enum MouseGoal {
  ;
  public static void simple( //
      OwlAnimationFrame owlAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint) {
    simple(owlAnimationFrame, trajectoryEntity, plannerConstraint, Collections.emptyList());
  }

  public static void simple( //
      OwlAnimationFrame owlAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, //
      List<GlcPlannerCallback> _callbacks) {
    List<GlcPlannerCallback> callbacks = new ArrayList<>(_callbacks);
    if (trajectoryEntity instanceof GlcPlannerCallback)
      callbacks.add((GlcPlannerCallback) trajectoryEntity);
    callbacks.add(EntityGlcPlannerCallback.of(trajectoryEntity));
    supply(owlAnimationFrame.geometricComponent, //
        new SimpleGoalConsumer(trajectoryEntity, plannerConstraint, callbacks));
  }

  public static void simpleRrts( //
      OwlAnimationFrame owlAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint) {
    simpleRrts(owlAnimationFrame, trajectoryEntity, plannerConstraint, Collections.emptyList());
  }

  public static void simpleRrts( //
      OwlAnimationFrame owlAnimationFrame, TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, //
      List<RrtsPlannerCallback> _callbacks) {
    List<RrtsPlannerCallback> callbacks = new ArrayList<>(_callbacks);
    if (trajectoryEntity instanceof RrtsPlannerCallback)
      callbacks.add((RrtsPlannerCallback) trajectoryEntity);
    supply(owlAnimationFrame.geometricComponent, //
        new SimpleGoalConsumer(trajectoryEntity, plannerConstraint, callbacks));
  }

  public static void supply(GeometricComponent geometricComponent, GoalConsumer goalConsumer) {
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent mouseEvent) {
        final int mods = mouseEvent.getModifiersEx();
        final int mask = InputEvent.CTRL_DOWN_MASK; // 128 = 2^7
        if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
          if ((mods & mask) == 0) // no ctrl pressed
            goalConsumer.accept(geometricComponent.getMouseSe2CState());
        }
      }
    };
    geometricComponent.jComponent.addMouseListener(mouseAdapter);
  }
}
