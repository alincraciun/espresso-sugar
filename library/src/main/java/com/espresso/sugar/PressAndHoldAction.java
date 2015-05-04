package com.espresso.sugar;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.*;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.MotionEvent;
import android.view.View;
import org.hamcrest.Matcher;

import javax.annotation.Nonnull;

import static org.hamcrest.Matchers.anything;

public class PressAndHoldAction {
    @Nonnull private final DragContext dragContext;
    @Nonnull private PrecisionDescriber precisionDescriber = Press.FINGER;
    @Nonnull private CoordinatesProvider coordinatesProvider = GeneralLocation.CENTER;

    public static PressAndHoldAction pressAndHoldView(@Nonnull Matcher<View> viewMatcher) {
        return new PressAndHoldAction(viewMatcher).perform();
    }

    public static PressAndHoldAction pressAndHoldView(@Nonnull final DragContext dragContext) {
        return new PressAndHoldAction(dragContext).perform();
    }

    private PressAndHoldAction(@Nonnull Matcher<View> viewMatcher) {
        this(new DragContext(viewMatcher));
    }

    private PressAndHoldAction(@Nonnull final DragContext dragContext) {
        this.dragContext = dragContext;
    }

    public PressAndHoldAction until(@Nonnull final WaitCondition condition) {
        dragContext.perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return anything();
            }

            @Override
            public String getDescription() {
                return "until " + condition.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view) {
                while(!condition.isSatisfied()) {
                    uiController.loopMainThreadForAtLeast(50);
                }
            }
        });
        return this;
    }

    public PressAndHoldAction untilView(@Nonnull final Matcher<View> viewMatcher, @Nonnull final ViewWaitCondition condition) {
        dragContext.perform(viewMatcher, new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return anything();
            }

            @Override
            public String getDescription() {
                return "until " + condition.getDescription(dragContext.getViewMatcher());
            }

            @Override
            public void perform(UiController uiController, View view) {
                while (!condition.isSatisfied(view)) {
                    uiController.loopMainThreadForAtLeast(50);
                }
            }
        });
        return this;
    }

    public PressAndHoldAction untilIt(@Nonnull final ViewWaitCondition condition) {
        return untilView(dragContext.getViewMatcher(), condition);
    }


    public PressAndHoldAction then() {
        return this;
    }

    public DragContinuation drag() {
        return new DragContinuation(dragContext);
    }

    public void andDrop() {
        new DropAction(dragContext);
    }

    private PressAndHoldAction perform() {
        dragContext.perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isDisplayingAtLeast(90);
            }

            @Override
            public String getDescription() {
                return "drag";
            }

            @Override
            public void perform(UiController uiController, View view) {
                float[] coordinates = coordinatesProvider.calculateCoordinates(view);
                float[] precision = precisionDescriber.describePrecision();

                MotionEvent downEvent = MotionEvents2.sendDown(uiController, coordinates, precision).down;
                dragContext.setDownEvent(downEvent);
            }
        });
        return this;
    }
}
