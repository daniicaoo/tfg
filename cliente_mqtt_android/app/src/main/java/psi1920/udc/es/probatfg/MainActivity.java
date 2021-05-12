package psi1920.udc.es.probatfg;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    static final int REQUEST_CODE_MAIN = 1;
    public static final String CODIGO_ACTIVAR = "active";
    public static final String EXTRA_MESSAGE = "";
    private static final String SUCCESS = "CONEXION EXITOSA";
    private static final String FAILFURE = "CONEXION FALLIDA";
    private static final String TAC = "SALIDA";
    private Button button_ok;
    private Button button_add;
    private Button button_activateDoor;
    private EditText editText;

    private  String topic_subscribe = "nI48fpMX0Qb37oj/output";
    private int qos_topic = 1;

    private  String topic_publish = "nI48fpMX0Qb37oj/input";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_ok = (Button) findViewById(R.id.but_ok);
        button_ok.setOnClickListener(this);

        editText = (EditText) findViewById(R.id.et_text);

        //Collo o intent de cando me desconecto do broker e volvo a pantalla de inicio
        Intent intent = getIntent();
        String message = intent.getStringExtra(ControlActivity.EXTRA_MESSAGE); //mainactivity porque pillo o intent de main
        if (message!=null) {
            Log.d("COMPROBACIONS", "Mensaxe de ControlActivity: " + message);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == button_ok) { //boton para activar mecanismo da porta
            connectBroker();
        }

    }

    //Implemento intent a ControlActivity, que sera onde se cree a conexion e se faga o control da porta.

    public void connectBroker() {

        Intent intentControlActivity = new Intent(this, ControlActivity.class);



        String message = CODIGO_ACTIVAR;
        intentControlActivity.putExtra(EXTRA_MESSAGE, message); //envio o texto do edit text como informacion extra
        startActivityForResult(intentControlActivity,REQUEST_CODE_MAIN);

        Log.d("COMPROBACIONS","Intent hacia actividade Param.");


    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String message;
        if (requestCode == REQUEST_CODE_MAIN && resultCode == RESULT_OK) {
            message = data.getStringExtra("message");
            Log.d("COMPROBACIONS", "ParamActivity devolveme: " + message);
            editText.setText(message);
        }
    }




}