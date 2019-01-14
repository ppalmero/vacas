package info.androidhive.bottomsheet;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import bdet.bitacora.BloqueBitacora;
import bdet.bitacora.DatoBitacora;
import bdet.comun.Constantes;
import bdet.comun.Punto;
import bdet.comun.Rectangulo;
import bdet.dsrtree.DSRTree;
import bdet.rtree.Dato;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.androidhive.bottomsheet.enums.Consultas;
import info.androidhive.bottomsheet.views.Teselado;
import info.androidhive.bottomsheet.ws.callWS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.expandir)
    Button btnExpandir;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    /*** circle view ***/

    CircleProgressView mCircleView;
    Switch mSwitchSpin;
    Switch mSwitchShowUnit;
    SeekBar mSeekBar;
    SeekBar mSeekBarSpinnerLength;
    Boolean mShowUnit = true;
    Spinner mSpinner;
    private int tiempoActual;

    /***fin circle view ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //obtenerDatos();
        //inicializarEstructura();

        // Capture our button from layout
        ImageButton btnInicio = findViewById(R.id.btnInicio);
        // Register the onClick listener with the implementation above
        btnInicio.setOnClickListener(btnInicioListener);

        ImageButton btnAnterior = findViewById(R.id.btnAnterior);
        btnAnterior.setOnClickListener(btnAnteriorListener);

        ImageButton btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(btnSiguienteListener);

        ImageButton btnFin = findViewById(R.id.btnFin);
        btnFin.setOnClickListener(btnFinListener);

        cargarCirculos();

        /*** LECTURA DE POSICIÓN INICIAL - USO DE WEB SERVICE Y JSON ***/
        callWS cws = new callWS();
        try {
            tiempoActual = 0;
            JSONObject reader = new JSONObject(cws.requestWS(Consultas.INICIO, null, getApplicationContext()));
            //cargarPosicionInicial(reader);
            //String sys  = reader.getString("Tiempo");
            JSONObject puntos = reader.getJSONObject("Puntos");
            Map<Integer, Vaca> vacas = new HashMap<>();
            for (int i = 0; i < puntos.length(); i++) {
                JSONObject vaca = puntos.getJSONObject("Punto" + i);
                vacas.put(i, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
            }
            sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
            sheetBehavior.setHideable(false);
            sheetBehavior.setSkipCollapsed(false);
            Teselado t = (Teselado)((LinearLayout)((CardView)layoutBottomSheet.getChildAt(1)).getChildAt(0)).getChildAt(0);
            t.setVacas(vacas);
            t.drawVacas(true);
        } catch (JSONException e) {
            Toast toast = Toast.makeText(getApplicationContext(), "JSON MAlformado " + e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        //btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                /*Context context = getApplicationContext();
                CharSequence text = "Hello toast!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
            }
        });
    }

    private void cargarPosicionInicial(JSONObject reader) {
        //si se trabaja con d*rtree en el celular
    }

    private void obtenerDatos() {

    }

    private void inicializarEstructura() {
        String por;
        String bloq = "" + Constantes.TAMANO_BITACORA;
        String Cantidad = "300";
        //salida = new PrintWriter(new BufferedWriter(new FileWriter("C:/Datos/Documentos Gise/Trabajo/Trabajo Tesis/EspacioTemporales/Informe-actividades/informe final/pruebaslala/DSRTREE/evento/evento_5/" + Cantidad + "/DS" + bloq)));
        por = "7";
        String str[] = {"datos/prueba_" + Cantidad + "_" + por + "", "consultas/evento_5", Cantidad, por, "pruebas/eventoLala/" + bloq + "bloques/sal_evento_5_" + Cantidad + "_" + por + "_40"};
        if (str.length != 5) {
            System.out.println("Error. Parámetros incorrectos");
            System.exit(1);
        }
        try{
        // Archivo con datos de prueba
        BufferedReader datosEntrada = new BufferedReader(new FileReader(str[0]));

        // Archivo con consultas
        //BufferedReader consultas = new BufferedReader(new FileReader(str[1]));

        // N�mero de objetos del archivo de prueba
        int numeroObjetos = Integer.parseInt(str[2]);

        // Arreglo con todos los objetos para instante de tiempo dado
        Dato[] datosRtree = new Dato[numeroObjetos];

        // Porcentaje de mov
        float porcMov = (float) (Integer.parseInt(str[3]) / 100.0);

        // Crea un nuevo DRSTree
        DSRTree dsr = new DSRTree(numeroObjetos);

        // Archivo de salida
            /*PrintWriter salida1 = new PrintWriter(
                    new BufferedWriter(new FileWriter(str[4])));
             */
        String lineaActual;

        int oid; // OID del objeto
        float tiempo = 0; // tiempo en que se produce el movimiento
        float x1, y1 = 0; // "MBR" del objeto
        int i = 0; //para iterar
        int porcentajeMov;
        while ((i < numeroObjetos) && (lineaActual = datosEntrada.readLine()) != null) {

            StringTokenizer s = new StringTokenizer(lineaActual);

            // Se leen los datos de la l�nea actual
            oid = Integer.parseInt(s.nextToken()) - 1;
            tiempo = Float.parseFloat(s.nextToken()) * 100;
            x1 = Float.parseFloat(s.nextToken());
            y1 = Float.parseFloat(s.nextToken());
//	        x1 = Float.parseFloat(s.nextToken());
//	        y1 = Float.parseFloat(s.nextToken());
            //tiempo = Float.parseFloat(s.nextToken());

            //int porcenta=Integer.parseInt(s.nextToken());
            //System.out.println(oid +" "+ x1 +" "+ y1 +" "+ tiempo);
            datosRtree[i++] = new Dato(new Rectangulo(x1, y1, x1, y1), oid);
            dsr.getC().getEntradaC(oid).setPosInicial(x1, y1);
        }
        dsr.cargaInicial(datosRtree);
        i = 1;
        float tiempoViejo = 0;
        ArrayList<Float> momentos = new ArrayList<>();
        while ((lineaActual = datosEntrada.readLine()) != null) {

            StringTokenizer s = new StringTokenizer(lineaActual);
            int indA = dsr.getA().getIndice();

            // Se leen los datos de la l�nea actual
            oid = Integer.parseInt(s.nextToken()) - 1;
            tiempo = Float.parseFloat(s.nextToken()) * 100;
            x1 = Float.parseFloat(s.nextToken());
            y1 = Float.parseFloat(s.nextToken());
                /*if (tiempo == 104.0){
                    break;
                }*/
            if (tiempo != tiempoViejo) {
                try{
                    if (dsr.getA().entradasA[indA].getBitacora().estaLlenaBit(porcMov * numeroObjetos)) {
                        System.out.println("OBJETOS INSERTADOS -------- " + i);
                        dsr.actualizar(tiempo, indA, datosRtree);
                    }
                }catch (ArrayIndexOutOfBoundsException ex){
                    System.out.println(ex.getMessage());
                }
                BloqueBitacora bloque[] = dsr.getA().getEntradaA(dsr.getA().getIndice()).getBitacora().getBloques();
                int indiceBloque = bloque[dsr.getA().getEntradaA(dsr.getA().getIndice()).getBitacora().getIndiceActual()].getIndiceBloque();
                if (indiceBloque == Constantes.TAMANO_BLOQUE_BITACORA) {
                    indiceBloque = 0;
                }
                dsr.getB().agregarEntrada(tiempo,
                        dsr.getA().getIndice(),
                        dsr.getA().getEntradaA(dsr.getA().getIndice()).getBitacora().getIndiceActual(),
                        indiceBloque);
            }
            momentos.add(tiempoViejo);
            tiempoViejo = tiempo;

            Punto pos = new Punto(x1, y1);
            DatoBitacora dato = new DatoBitacora(oid, tiempo, pos, null, dsr.getA().indiceA, dsr.getA().getEntradaA(dsr.getA().indiceA).getBitacora().getIndiceActual());
            try {
                dsr.cargaMovimiento(dato);
            } catch (ArrayIndexOutOfBoundsException ex){
                System.out.println(i);
            }
            i++;
        }
        /* LECTURA DE DATOS RELACIONALES

        try {
            NetworkServerControl server = new NetworkServerControl();
            server.start (null);
            String driver = "org.apache.derby.jdbc.ClientDriver";
            Class.forName(driver).newInstance();
            String dbName = "SegETRodeo";
            String connectionURL = "jdbc:derby://localhost:1527/" + dbName + "";

            conn = DriverManager.getConnection(connectionURL, "root", "GCyBD2017");
        } catch (SQLException ex) {
            Logger.getLogger(FramePruebas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SegETRodeo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(SegETRodeo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SegETRodeo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SegETRodeo.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener btnInicioListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Inicio", Toast.LENGTH_LONG);
            toast.show();
        }
    };

    private View.OnClickListener btnAnteriorListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Anterior", Toast.LENGTH_LONG);
            toast.show();
            callWS cws = new callWS();
            try {
                Map<String, Integer> parametrosWS = new HashMap<String, Integer>();
                parametrosWS.put("tiempo", tiempoActual - 1);

                JSONObject reader = new JSONObject(cws.requestWSs(Consultas.ANTERIOR, parametrosWS, getApplicationContext()));
                JSONObject puntos = reader.getJSONObject("Puntos");
                modificarVista(puntos);
                tiempoActual--;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener btnSiguienteListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Siguiente", Toast.LENGTH_SHORT);
            toast.show();
            callWS cws = new callWS();
            try {
                Map<String, Integer> parametrosWS = new HashMap<String, Integer>();
                parametrosWS.put("tiempo", tiempoActual + 1);

                JSONObject reader = new JSONObject(cws.requestWSs(Consultas.SIGUIENTE, parametrosWS, getApplicationContext()));
                JSONObject puntos = reader.getJSONObject("Puntos");
                modificarVista(puntos);
                tiempoActual++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void modificarVista(JSONObject puntos) throws JSONException{
        Map<Integer, Vaca> vacas = new HashMap<>();
        for (int i = 0; i < puntos.length(); i++) {
            Integer oId = Integer.parseInt(((String)puntos.names().get(i)).substring(5));
            JSONObject vaca = puntos.getJSONObject("Punto" + oId);
            vacas.put(oId, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
        }
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setHideable(false);
        sheetBehavior.setSkipCollapsed(false);
        Teselado t = (Teselado) findViewById(R.id.teseladoView);
        t.setVacasModificadas(vacas);
        t.drawVacas(true);
    }

    private View.OnClickListener btnFinListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Fin", Toast.LENGTH_LONG);
            toast.show();
        }
    };

    private void cargarCirculos() {

        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d(TAG, "Progress Changed: " + value);
            }
        });

        //value setting
//        mCircleView.setMaxValue(100);
//        mCircleView.setValue(0);
//        mCircleView.setValueAnimated(24);

        //growing/rotating counter-clockwise
//        mCircleView.setDirection(Direction.CCW)

//        //show unit
//        mCircleView.setUnit("%");
//        mCircleView.setUnitVisible(mShowUnit);
//
//        //text sizes
//        mCircleView.setTextSize(50); // text size set, auto text size off
//        mCircleView.setUnitSize(40); // if i set the text size i also have to set the unit size
//        mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
//        //if you want the calculated text sizes to be bigger/smaller you can do so via
//        mCircleView.setUnitScale(0.9f);
//        mCircleView.setTextScale(0.9f);
//
////        //custom typeface
////        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ANDROID_ROBOT.ttf");
////        mCircleView.setTextTypeface(font);
////        mCircleView.setUnitTextTypeface(font);
//
//
//        //color
//        //you can use a gradient
//        mCircleView.setBarColor(getResources().getColor(R.color.primary), getResources().getColor(R.color.accent));
//
//        //colors of text and unit can be set via
//        mCircleView.setTextColor(Color.RED);
//        mCircleView.setTextColor(Color.BLUE);
//        //or to use the same color as in the gradient
//        mCircleView.setTextColorAuto(true); //previous set values are ignored
//
//        //text mode
//        mCircleView.setText("Text"); //shows the given text in the circle view
//        mCircleView.setTextMode(TextMode.TEXT); // Set text mode to text to show text
//
//        //in the following text modes, the text is ignored
//        mCircleView.setTextMode(TextMode.VALUE); // Shows the current value
//        mCircleView.setTextMode(TextMode.PERCENT); // Shows current percent of the current value from the max value

        //spinning
//        mCircleView.spin(); // start spinning
//        mCircleView.stopSpinning(); // stops spinning. Spinner gets shorter until it disappears.
//        mCircleView.setValueAnimated(24); // stops spinning. Spinner spins until on top. Then fills to set value.


        //animation callbacks

        //this example shows how to show a loading text if it is in spinning mode, and the current percent value otherwise.
        mCircleView.setShowTextWhileSpinning(true); // Show/hide text in spinning mode
        mCircleView.setText("Cargando...");
        mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                mCircleView.setUnitVisible(mShowUnit);
                                break;
                            case SPINNING:
                                mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );


        // region setup other ui elements
        //Setup Switch
        /*mSwitchSpin = (Switch) findViewById(R.id.switch1);
        mSwitchSpin.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mCircleView.spin();
                        } else {
                            mCircleView.stopSpinning();
                        }
                    }
                }

        );

        mSwitchShowUnit = (Switch) findViewById(R.id.switch2);
        mSwitchShowUnit.setChecked(mShowUnit);
        mSwitchShowUnit.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mCircleView.setUnitVisible(isChecked);
                        mShowUnit = isChecked;
                    }
                }

        );*/

        //Setup SeekBar
        /*mSeekBar = (SeekBar) findViewById(R.id.seekBar);

        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mCircleView.setValueAnimated(seekBar.getProgress(), 1500);
                        mSwitchSpin.setChecked(false);
                    }
                }
        );
/*
        mSeekBarSpinnerLength = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarSpinnerLength.setMax(360);
        mSeekBarSpinnerLength.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mCircleView.setSpinningBarLength(seekBar.getProgress());
                    }
                });

        mSpinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<>();
        list.add("Left Top");
        list.add("Left Bottom");
        list.add("Right Top");
        list.add("Right Bottom");
        list.add("Top");
        list.add("Bottom");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        mSpinner.setAdapter(dataAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mCircleView.setUnitPosition(UnitPosition.LEFT_TOP);
                        break;
                    case 1:
                        mCircleView.setUnitPosition(UnitPosition.LEFT_BOTTOM);
                        break;
                    case 2:
                        mCircleView.setUnitPosition(UnitPosition.RIGHT_TOP);
                        break;
                    case 3:
                        mCircleView.setUnitPosition(UnitPosition.RIGHT_BOTTOM);
                        break;
                    case 4:
                        mCircleView.setUnitPosition(UnitPosition.TOP);
                        break;
                    case 5:
                        mCircleView.setUnitPosition(UnitPosition.BOTTOM);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner.setSelection(2);*/
    }

    /**
     * manually opening / closing bottom sheet on button click
     *
    @OnClick(R.id.btn_bottom_sheet)
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            btnBottomSheet.setText("Expand sheet");
        }
    }*/

    @OnClick(R.id.expandir)
    public void expandirBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            //btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            //btnBottomSheet.setText("Expand sheet");
        }
    }

    /**
     * showing bottom sheet dialog
     *
    @OnClick(R.id.btn_bottom_sheet_dialog)
    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }


    /**
     * showing bottom sheet dialog fragment
     * same layout is used in both dialog and dialog fragment
     *
    @OnClick(R.id.btn_bottom_sheet_dialog_fragment)
    public void showBottomSheetDialogFragment() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }*/
}
