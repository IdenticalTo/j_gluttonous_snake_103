package com.flyingBird.www.entity;

public class Grade {
    private String score;
    private String useTime;

    public Grade(){}
    public Grade(String score, String useTime) {
        this.score = score;
        this.useTime = useTime;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return score + ',' + useTime;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }
}
