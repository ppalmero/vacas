package info.androidhive.bottomsheet.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
    float xDown, yDown;
    private boolean dibujarCirculo = false;
    private boolean dibujarVacas;
    private Map<Integer, Vaca> vacas;
    private Map<Integer, Vaca> vacasModificadas = new HashMap<>();
    private Map<Integer, Vaca> vacasIn;
    private Map<Integer, Vaca> vacasOut;
    private Map<Integer, Vaca> oldsVacas = new HashMap<>();

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
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLUE);

        float ancho = canvas.getWidth() / 1000f;
        float alto  = canvas.getHeight() / 1000f;

        if (accion == "down"){
            //path.moveTo(x, y);
            xDown = x;
            yDown = y;
        }
        if (accion == "move"){
            //path.lineTo(x, y);
            path.reset();
            if ((x < xDown) && (y < yDown)) {
                path.addRect(x, y, xDown, yDown, Path.Direction.CW);
            } else if (x < xDown) {
                path.addRect(x, yDown, xDown, y, Path.Direction.CW);
            } else if (y < yDown) {
                path.addRect(xDown, y, x, yDown, Path.Direction.CW);
            } else {
                path.addRect(xDown, yDown, x, y, Path.Direction.CW);
            }
        }
        canvas.drawPath(path, paint);

        if (dibujarVacas){
            //canvas.drawCircle(30/2, 30/2, 30, paint);
            Drawable d = getResources().getDrawable(R.drawable.vaca_actionbar, null);
            System.out.println("Cantidad de vacas modificadas: " + vacasModificadas.size());
            for (Integer i : vacas.keySet()) {
                if (vacasModificadas.containsKey(i)){
                    d.setBounds((int) (vacasModificadas.get(i).getX() * ancho), (int) (vacasModificadas.get(i).getY() * alto), (int)((vacasModificadas.get(i).getX() + d.getIntrinsicWidth()) * ancho), (int)((vacasModificadas.get(i).getY() + d.getIntrinsicHeight()) * alto));
                    d.draw(canvas);
                    //TODO DIBUJAR FLECHA

                    if (vacas.get(i).getX() != vacasModificadas.get(i).getX() || vacas.get(i).getY() != vacasModificadas.get(i).getY()) {
                        oldsVacas.put(i, vacas.put(i, vacasModificadas.get(i)));
                    }

                    if (oldsVacas.get(i) != null) {//CONDICION AGREGADA PARA ARREGLAR ERROR DE IR AL FINAL Y MOVER ANTERIOR
                        System.out.println("Vaca modificada: " + i + " - (X: " + oldsVacas.get(i).getX() + ", " + oldsVacas.get(i).getY() + ")"
                                + " -> (X: " + vacasModificadas.get(i).getX() + ", " + vacasModificadas.get(i).getY() + ")");
                        canvas.drawCircle((oldsVacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (oldsVacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, 4, paint);
                        canvas.drawLine((oldsVacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (oldsVacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, (vacasModificadas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacasModificadas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, paint);
                    }
                } else {
                    d.setBounds((int) (vacas.get(i).getX() * ancho), (int) (vacas.get(i).getY() * alto), (int)((vacas.get(i).getX() + d.getIntrinsicWidth()) * ancho), (int) ((vacas.get(i).getY() + d.getIntrinsicHeight()) * alto));
                    d.draw(canvas);
                }
            }
        }
        return;
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

    public void drawVacasInicio(boolean dibujar) {
        dibujarVacas = dibujar;
        this.vacasModificadas.clear();
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
