package android.app.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Gesture implements Serializable {
    private transient Bitmap bitmap;
    private String name;
    private float[] xCoordinates;
    private float[] yCoordinates;

    public Gesture(Bitmap bitmap, float[] xCoordinates, float[] yCoordinates) {
        this.bitmap = bitmap;
        this.xCoordinates = xCoordinates;
        this.yCoordinates = yCoordinates;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float similarityScore(Gesture other) {
        float score = 0;

        for (int i = 0; i < other.xCoordinates.length; ++i) {
            float dx = xCoordinates[i] - other.xCoordinates[i];
            float dy = yCoordinates[i] - other.yCoordinates[i];
            score += Math.sqrt(dx * dx + dy * dy);
        }

        return score / other.xCoordinates.length;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
        byte bitmapBytes[] = byteStream.toByteArray();
        stream.write(bitmapBytes, 0, bitmapBytes.length);
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int value;

        while ((value = stream.read()) != -1) {
            byteStream.write(value);
        }

        byte bytes[] = byteStream.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
