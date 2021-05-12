package android.app.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GestureView extends View {
    private Canvas canvas;
    private Path path;
    private Paint pathPaint;
    private Bitmap bitmap;
    private Paint bitmapPaint;
    private Path circlePath;
    private Paint circlePaint;
    private float previousX;
    private float previousY;
    private float firstX;
    private float firstY;
    private int width;
    private int height;
    private GestureEvent drawnEvent;
    private Gesture current;
    private Paint backgroundPaint;
    private RectF thumbnailArea;

    public interface GestureEvent {
        void invoke();
    }

    public GestureView(Context context, AttributeSet attributes) {
        super(context, attributes);
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setDither(true);
        pathPaint.setColor(Color.BLACK);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setStrokeWidth(18);
        path = new Path();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePath = new Path();
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.CYAN);
        backgroundPaint.setStyle(Paint.Style.FILL);
        thumbnailArea = new RectF(0, 0, 200, 200);
        drawnEvent = null;
        current = null;
    }

    public void subscribeDrawnEvent(GestureEvent event) {
        drawnEvent = event;
    }

    public void clear() {
        onSizeChanged(width, height, width, height);
        invalidate();
    }

    public Gesture save(String name) {
        current.setName(name);
        return current;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.width = width;
        this.height = height;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, pathPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clear();
                path.reset();
                path.moveTo(x, y);
                previousX = x;
                previousY = y;
                firstX = x;
                firstY = y;
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - previousX) >= 5 || Math.abs(y - previousY) >= 5) {
                    path.quadTo(previousX, previousY, (x + previousX) / 2, (y + previousY) / 2);
                    previousX = x;
                    previousY = y;
                    circlePath.reset();
                    circlePath.addCircle(previousX, previousY, 30, Path.Direction.CW);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                path.lineTo(previousX, previousY);
                circlePath.reset();
                current = generateGesture(generateThumbnail());
                canvas.drawPath(path,  pathPaint);
                path.reset();
                invalidate();
                if (drawnEvent != null) {
                    drawnEvent.invoke();
                }
                break;
        }

        return true;
    }

    private Bitmap generateThumbnail() {
        Path thumbnail = new Path(path);
        RectF bounds = new RectF();
        float ratio = 1;

        thumbnail.computeBounds(bounds, false);

        if (bounds.width() > 0 || bounds.height() > 0) {
            ratio = 160 / Math.max(bounds.width(), bounds.height());
        }

        Matrix matrix = new Matrix();
        matrix.setScale(ratio, ratio);

        if (bounds.width() > 0 || bounds.height() > 0) {
            thumbnail.offset(-bounds.left, -bounds.top);
        } else {
            thumbnail.offset(-firstX + 100, -firstY + 100);
        }

        thumbnail.transform(matrix);
        thumbnail.computeBounds(bounds, false);

        Bitmap thumbnailBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);

        if (bounds.width() > 0 || bounds.height() > 0) {
            thumbnail.offset((200 - bounds.width()) / 2, (200 - bounds.height()) / 2);
        }

        Canvas thumbnailCanvas = new Canvas();
        thumbnailCanvas.setBitmap(thumbnailBitmap);
        thumbnailCanvas.drawRoundRect(thumbnailArea, 25, 25, backgroundPaint);
        thumbnailCanvas.drawPath(thumbnail, pathPaint);

        return thumbnailBitmap;
    }

    private Gesture generateGesture(Bitmap thumbnail) {
        int sampleSize = 150;
        float[] position = new float[2];
        Path pathCopy = new Path(path);
        PathMeasure pathMeasure = new PathMeasure(pathCopy, false);
        float pathLength = pathMeasure.getLength();
        float centerX = 0;
        float centerY = 0;

        for (int i = 0; i < sampleSize; ++i) {
            float distance = pathLength * i / (sampleSize - 1);
            pathMeasure.getPosTan(distance, position, null);
            centerX += position[0];
            centerY += position[1];
        }

        centerX /= sampleSize;
        centerY /= sampleSize;

        pathCopy.offset(-centerX, -centerY);

        Matrix matrix = new Matrix();
        matrix.setRotate(-(float)Math.toDegrees(Math.atan2(firstY - centerY, firstX - centerX)));

        pathCopy.transform(matrix);

        RectF bounds = new RectF();
        pathCopy.computeBounds(bounds, false);
        float ratio = 1;

        if (bounds.width() > 0 || bounds.height() > 0) {
            ratio = 200 / Math.max(bounds.width(), bounds.height());
        }

        matrix = new Matrix();
        matrix.setScale(ratio, ratio);

        pathCopy.transform(matrix);

        float[] xCoordinates = new float[sampleSize];
        float[] yCoordinates = new float[sampleSize];

        pathMeasure = new PathMeasure(pathCopy, false);
        pathLength = pathMeasure.getLength();

        for (int i = 0; i < sampleSize; ++i) {
            float distance = pathLength * i / (sampleSize - 1);
            pathMeasure.getPosTan(distance, position, null);
            xCoordinates[i] = position[0];
            yCoordinates[i] = position[1];
        }

        return new Gesture(thumbnail, xCoordinates, yCoordinates);
    }
}
