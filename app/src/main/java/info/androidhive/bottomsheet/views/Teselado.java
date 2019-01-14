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
import java.util.HashMap;
import java.util.Map;

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
    private Map<Integer, Vaca> vacas;
    private Map<Integer, Vaca> vacasModificadas = new HashMap<>();
    private Map<Integer, Vaca> vacasIn;
    private Map<Integer, Vaca> vacasOut;

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
            for (Integer i : vacas.keySet()) {
                if (vacasModificadas.containsKey(i)){
                    d.setBounds(vacasModificadas.get(i).getX(), vacasModificadas.get(i).getY(), vacasModificadas.get(i).getX() + d.getIntrinsicWidth(), vacasModificadas.get(i).getY() + d.getIntrinsicHeight());
                    d.draw(canvas);
                    //TODO DIBUJAR FLECHA

                    Vaca oldValue = vacas.put(i, vacasModificadas.get(i));
                    canvas.drawCircle(oldValue.getX()-2, oldValue.getY()-2, 5, paint);
                    canvas.drawLine(oldValue.getX(), oldValue.getY(),vacasModificadas.get(i).getX(), vacasModificadas.get(i).getY(), paint);
                } else {
                    d.setBounds(vacas.get(i).getX(), vacas.get(i).getY(), vacas.get(i).getX() + d.getIntrinsicWidth(), vacas.get(i).getY() + d.getIntrinsicHeight());
                    d.draw(canvas);
                }
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
        this.invalidate(); //PARA REDIBUJAR
    }

    public void setVacas(Map<Integer, Vaca> vacas) {
        this.vacas = vacas;
    }

    public void addVaca(Integer id, Vaca v){
        this.vacas.put(id, v);
    }

    public void setVacasModificadas (Map<Integer, Vaca> vacas) {
        this.vacasModificadas = vacas;
    }
}
