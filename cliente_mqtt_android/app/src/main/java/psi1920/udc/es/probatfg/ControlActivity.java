package psi1920.udc.es.probatfg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import androidx.appcompat.app.AppCompatActivity;

public class ControlActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String SUCCESS = "CONEXION EXITOSA";
    private static final String FAILFURE = "CONEXION FALLIDA";
    private static final String TAC = "SALIDA";
    public static final String CODIGO_ACTIVAR = "active";
    public static final String PASSWORD = "55QNKPsXM3gGabh";
    public static final String USER_NAME = "xMLxlEHA3e3hcfF";
    public static final String ACTIVATE_DOOR = "activate_door";
    public static final String EXTRA_MESSAGE = "broker_disconnect";
    private Button button_add;
    private Button button_activateDoor;
    private Button button_desactivateDoor;
    private  String topic_publish = "nI48fpMX0Qb37oj/input";
    private EditText editTextP;

    private  String topic_suscribe_pulsador = "nI48fpMX0Qb37oj/output";

    private String porta = "";
    /*------------------------------------------------------------------------------------------------*/

    private final String TAG = "AiotMqtt";
/*
    final private String PRODUCTKEY = "a11xsrWmW14";
    final private String DEVICENAME = "paho_android";
    final private String DEVICESECRET = "tLMT9QWD36U2SArglGqcHCDK9rK9nOrA";


    final private String PUB_TOPIC =  "nI48fpMX0Qb37oj/input";

    final private String SUB_TOPIC = "nI48fpMX0Qb37oj/output";
*/
    final String host = "tcp://ioticos.org:1883";
    private String clientId;


    MqttAndroidClient mqttAndroidClient;
    /*------------------------------------------------------------------------------------------------*/




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        button_activateDoor = (Button) findViewById(R.id.but_activateDoor);
        button_desactivateDoor = (Button) findViewById(R.id.button_desactivateDoor);
        button_add = (Button) findViewById(R.id.but_addSuscription);

        button_activateDoor.setOnClickListener(this);
        button_desactivateDoor.setOnClickListener(this);
        button_add.setOnClickListener(this);

        editTextP = (EditText) findViewById(R.id.et_text_p);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE); //mainactivity porque pillo o intent de main activity
        Log.d("COMPROBACIONS","Recibo intent co mensaxe:" + message );
        // capturo o texto e presento en pantalla
        if ((message!=null)&&(message.equals(CODIGO_ACTIVAR) )) {
           connectBroker();
        }else{
            Log.d("COMPROBACIONS"," " );
        }
    }



    @Override
    public void onClick(View v) {
        if (v == button_activateDoor){ //boton para activar mecanismo da porta
            activateDoor(topic_publish, mqttAndroidClient);
        }else if (v == button_add){
            suscribePulsador(porta);
        }else if (v == button_desactivateDoor){
            disconnect();
        }
    }
/*
    public void connectBroker() {


        clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), host,
                        clientId);
        MqttConnectOptions options = new MqttConnectOptions();

        options.setUserName(USER_NAME);
        options.setPassword(PASSWORD.toCharArray());
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("COMPROBACIONS", "Conexión exitosa, Cliente: " + client);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("COMPROBACIONS", "Fallo de conexión");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
*/
    public void connectBroker() {
        clientId = MqttClient.generateClientId();
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(USER_NAME);
        mqttConnectOptions.setPassword(PASSWORD.toCharArray());



        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), host, clientId);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i(TAG, "connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                //Aqui chegan as notificacions das distintas suscripcions que ten a aplicacion
                String msg = new String(message.getPayload());
                notify(topic,msg);
                Log.d(TAG, "topic: " + topic + ", msg: " + new String(message.getPayload()));
            }

            private void notify(String topic, String msg) {

                if (topic.equals("nI48fpMX0Qb37oj/output")) {
                    Toast toastsuscribe =
                            Toast.makeText(getApplicationContext(),
                                    "Pulsador activado: " + msg, Toast.LENGTH_SHORT);

                    toastsuscribe.show();
                }

            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "msg delivered");
            }
        });

        try {
            mqttAndroidClient.connect(mqttConnectOptions,ControlActivity.this, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG, "connect succeed");


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i(TAG, "connect failed");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }



    }




    //Funcion utilizada para cerrar o abrir la puerta, recibe el parametro de que puerte queremos abrir

    private void activateDoor(String topic, MqttAndroidClient client){

        byte[] encodeMessage = new byte[0];

         try {
             encodeMessage = ACTIVATE_DOOR.getBytes("UTF-8");
             MqttMessage message = new MqttMessage(encodeMessage);
             Log.d("COMPROBACIONS FALLOS","Topic: " + topic_publish );
             Log.d("COMPROBACIONS FALLOS","Mensaje: " + message );
             Log.d("COMPROBACIONS FALLOS","Cliente: " + client );
             client.publish(topic_publish, message);
             Log.d("COMPROBACIONS","Activacion da porta feita." );

        } catch (MqttException | UnsupportedEncodingException e) {
            e.printStackTrace();
             Log.d("COMPROBACIONS","Fallo de activacion da porta." );
        }

    }

    /*
    public void publishMessage(String payload) {
        try {
            if (mqttAndroidClient.isConnected() == false) {
                mqttAndroidClient.connect();
            }

            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(0);
            mqttAndroidClient.publish(PUB_TOPIC, message,"activarrrr", new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "publish succeed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "publish failed!");
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
*/

    public void disconnect(){

        try {
            IMqttToken disconToken = mqttAndroidClient.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected

                    Intent data = new Intent();

                    String message = "Finish conexion.";
                    data.putExtra("message", message); //envio o texto do edit text como informacion extra
                    setResult(RESULT_OK, data);

                    Log.d("COMPROBACIONS","devolvo intent ao pulsar ok" );
                    finish();

                    Log.d(TAG, "Desconectado con exito.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                    Log.d(TAG, "No se pudo desconectar.");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void suscribePulsador(String porta){

        String topic = topic_suscribe_pulsador;
        int qos = 1;
        try {
            IMqttToken subToken = mqttAndroidClient.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d(TAG, "Suscripción con éxito.");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d(TAG, "Fallo de suscripción.");
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void unsuscribePulsador(){

        final String topic = topic_suscribe_pulsador;
        try {
            IMqttToken unsubToken = mqttAndroidClient.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }



}