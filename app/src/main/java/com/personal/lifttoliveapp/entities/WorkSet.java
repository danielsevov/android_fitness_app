package com.personal.lifttoliveapp.entities;

import java.util.ArrayList;
import java.util.Objects;

public class WorkSet {
    private String kilos, reps;
    private boolean isComplete;

    public WorkSet(String kilos, String reps) {
        this.kilos = kilos;
        this.reps = reps;
        this.isComplete = false;
    }

    public WorkSet(String reps, String kilos, boolean complete) {
        this.kilos = kilos;
        this.reps = reps;
        this.isComplete = complete;
    }

    @Override
    public String toString() {
        return reps + " Reps X " + kilos + " KGs";
    }

    public String getKilos() {
        return kilos;
    }

    public void setKilos(String kilos) {
        this.kilos = kilos;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public WorkSet() {
    }
}
