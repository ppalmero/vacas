package info.androidhive.bottomsheet.ws;

import android.content.Context;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Map;

import info.androidhive.bottomsheet.enums.Consultas;
import info.androidhive.bottomsheet.enums.Wss;

public class callWS {
    /** Called when the activity is first created.
     * URL: It is the url of WSDL file.
     *
     * NAMESPACE: The targetNamespace in WSDL.
     *
     * METHOD_NAME: It is the method name in web service. We can have several methods.
     *
     * SOAP_ACTION: NAMESPACE + METHOD_NAME.*/

    //private static String SOAP_ACTION1 = "http://ws.prp.com/datosIniciales";
    private static String NAMESPACE = "http://ws.prp.com/";
    private static String METHOD_NAME1 = "posicionInicial";
    private static String URL = "http://190.122.229.62:8080/MyWS/TestWS?wsdl";

    public String requestWS(Consultas parametro, Map<String, Integer> parametrosWS, Context context){
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
        //Use this to add parameters
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("arg0");
        propertyInfo.setValue(parametro);
        propertyInfo.setType(String.class);
        //request.addProperty(propertyInfo);
        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        //envelope.dotNet = true;
        try {
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.call(NAMESPACE + METHOD_NAME1, envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
            String result = soapPrimitive.toString();
            if(result != null) {
                //Get the first property and change the label text
                return result;//.getProperty(0).toString();
            } else {
                Toast.makeText(context, "No Response",Toast.LENGTH_LONG).show();
                return "retorna nada";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error" + e.getMessage();
        }
    }

    public String requestWSs(Consultas parametro, Map<String, Integer> parametrosWS, Context context){
        SoapObject request = null;
        String wsNombre = "";
        SoapSerializationEnvelope envelope;
        HttpTransportSE httpTransportSE;
        SoapPrimitive soapPrimitive;
        PropertyInfo propertyInfo;
        String result;
        switch (parametro){
            case INICIO:
                wsNombre =  Wss.posicionInicial.name();
                request = new SoapObject(NAMESPACE, wsNombre);
                break;
            case ANTERIOR:
                wsNombre = Wss.consultarMovimientoAnterior.name();
                request = new SoapObject(NAMESPACE, wsNombre);

                propertyInfo = new PropertyInfo();
                propertyInfo.setName("arg0");
                propertyInfo.setValue(parametrosWS.get("tiempo"));
                propertyInfo.setType(Integer.class);
                request.addProperty(propertyInfo);
                break;
            case SIGUIENTE:
                wsNombre = Wss.consultarMovimientoSiguiente.name();
                request = new SoapObject(NAMESPACE, wsNombre);

                propertyInfo = new PropertyInfo();
                propertyInfo.setName("arg0");
                propertyInfo.setValue(parametrosWS.get("tiempo"));
                propertyInfo.setType(Integer.class);
                request.addProperty(propertyInfo);
                break;
            case FIN:
                wsNombre =  Wss.posicionFinal.name();
                request = new SoapObject(NAMESPACE, wsNombre);
                break;
        }

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        try {
            httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.call(NAMESPACE + wsNombre, envelope);
            soapPrimitive = (SoapPrimitive)envelope.getResponse();
            result = soapPrimitive.toString();
            if(result != null) {
                return result;
            } else {
                Toast.makeText(context, "Not  Response",Toast.LENGTH_LONG).show();
                return "retorna nada";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error" + e.getMessage();
        }
    }
}
