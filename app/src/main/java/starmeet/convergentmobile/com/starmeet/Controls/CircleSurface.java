package starmeet.convergentmobile.com.starmeet.Controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;

@SuppressLint("ViewConstructor")
public class CircleSurface extends org.webrtc.SurfaceViewRenderer {
    private Path clipPath;

    public CircleSurface(Point position, int radius, Context context) {
        super(context);
        init(position, radius);
    }

    private void init(Point position, int radius) {
        clipPath = new Path();
        clipPath.addCircle(position.x, position.y, radius, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(clipPath);
        super.dispatchDraw(canvas);
    }
}
