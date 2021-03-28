package controllers;

import javafx.animation.Timeline;

import java.util.ArrayList;

public class Controller {
    private final ArrayList<Timeline> timelines;

    public Controller() {
        timelines = new ArrayList<>();
    }

    public ArrayList<Timeline> getTimelines() {
        return timelines;
    }

    public void addTimeline(Timeline timeline) {
        this.timelines.add(timeline);
    }
}
