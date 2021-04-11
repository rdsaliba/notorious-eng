package controllers;

import javafx.animation.Timeline;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final ArrayList<Timeline> timelines;

    public Controller() {
        timelines = new ArrayList<>();
    }

    public List<Timeline> getTimelines() {
        return timelines;
    }

    public void addTimeline(Timeline timeline) {
        this.timelines.add(timeline);
    }
}
