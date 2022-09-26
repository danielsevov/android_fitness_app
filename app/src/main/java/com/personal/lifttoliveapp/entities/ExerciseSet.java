package com.personal.lifttoliveapp.entities;

import java.util.ArrayList;

public class ExerciseSet {
    public String exercise, note;
    public ArrayList<WorkSet> workSets;

    public ExerciseSet(String exercise, String note) {
        this.exercise = exercise;
        this.note = note;
        workSets = new ArrayList<>();
        workSets.add(new WorkSet("0", "0"));
    }

    public ExerciseSet() {
        workSets = new ArrayList<>();
    }

    public ExerciseSet(String exercise, String note, ArrayList<WorkSet> newWorkSets) {
        this.exercise = exercise;
        this.note = note;
        workSets = newWorkSets;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<WorkSet> getWorksets() {
        return workSets;
    }

    public void setWorksets(ArrayList<WorkSet> workSets) {
        this.workSets = workSets;
    }

    @Override
    public String toString() {
        return "ExerciseSet{" +
                "exercise='" + exercise + '\'' +
                ", note='" + note + '\'' +
                ", workSets=" + workSets +
                '}';
    }
}
