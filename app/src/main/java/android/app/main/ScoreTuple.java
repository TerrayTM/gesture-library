package android.app.main;

public class ScoreTuple {
    private float score;
    private Gesture gesture;

    public ScoreTuple(float score, Gesture gesture) {
        this.score = score;
        this.gesture = gesture;
    }

    public float getScore() {
        return score;
    }

    public Gesture getGesture() {
        return gesture;
    }
}
