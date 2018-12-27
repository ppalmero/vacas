package info.androidhive.bottomsheet.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import info.androidhive.bottomsheet.R;
import info.androidhive.bottomsheet.Vaca;

/**
 * TODO: document your custom view class.
 */
public class Teselado extends View {

    Path path = new Path();
    String accion;
    float x = 50, y = 50;
    private boolean dibujarCirculo = false;
    private boolean dibujarVacas;
    private ArrayList<Vaca> vacas;

    public Teselado(Context context) {
        super(context);
    }

    public Teselado(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Teselado(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onDraw(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);

        int ancho = canvas.getWidth();

        if (accion == "down"){
            path.moveTo(x, y);
        }
        if (accion == "move"){
            path.lineTo(x, y);
        }
        canvas.drawPath(path, paint);

        if (dibujarVacas){
            //canvas.drawCircle(30/2, 30/2, 30, paint);
            Drawable d = getResources().getDrawable(R.drawable.vaca_actionbar, null);
            for (int i = 0; i < vacas.size(); i++) {
                d.setBounds(vacas.get(i).getX(), vacas.get(i).getY(), vacas.get(i).getX() + d.getIntrinsicWidth(), vacas.get(i).getY() + d.getIntrinsicHeight());
                d.draw(canvas);
            }
        }
    }

    public boolean onTouchEvent (MotionEvent me){
        x = me.getX();
        y = me.getY();

        if (me.getAction() == MotionEvent.ACTION_DOWN){
            accion = "down";
        }
        if (me.getAction() == MotionEvent.ACTION_MOVE){
            accion = "move";
        }

        invalidate();
        return true;
    }

    public void draw (boolean algo){
        dibujarCirculo = algo;
        invalidate();
    }

    public void drawVacas(boolean dibujar) {
        dibujarVacas = dibujar;
    }

    public void setVacas(ArrayList<Vaca> vacas) {
        this.vacas = vacas;
    }
}
