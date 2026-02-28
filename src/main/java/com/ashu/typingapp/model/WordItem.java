package com.ashu.typingapp.model;

public class WordItem {
    private String text;
    private double xPosition;
    private double yPosition;
    private boolean isDestroyed;

    // Constructor: Naya meteor word banane ke liye
    public WordItem(String text, double xPosition, double yPosition) {
        this.text = text;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.isDestroyed = false; // Shuru me word zinda rahega
    }

    // Getters
    public String getText() { return text; }
    public double getXPosition() { return xPosition; }
    public double getYPosition() { return yPosition; }
    public boolean isDestroyed() { return isDestroyed; }

    // Word ko screen pe neeche girane ke liye (Y position badhayenge)
    public void drop(double speed) {
        this.yPosition += speed;
    }

    // Jab user word ko sahi type kar dega, tab isko destroy karenge
    public void destroy() {
        this.isDestroyed = true;
    }
}