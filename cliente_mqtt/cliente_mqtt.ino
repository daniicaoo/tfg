#include <Arduino.h>
#include <WiFi.h>
#include <PubSubClient.h>


//**************************************
//*********** MQTT CONFIG **************
//const char *mqtt_server = "ioticos.org";
//const int mqtt_port = 1883;
//const char *mqtt_user = "xMLxlEHA3e3hcfF";
//const char *mqtt_pass = "55QNKPsXM3gGabh";
//const char *root_topic_subscribe = "nI48fpMX0Qb37oj/input";
//const char *root_topic_publish = "nI48fpMX0Qb37oj/output";
//**************************************
//*********** MQTT CONFIG HA**************
const char *mqtt_server = "192.168.1.36";
const int mqtt_port = 1883;
const char *mqtt_user = "broker_escoura";
const char *mqtt_pass = "escoura20";
const char *root_topic_subscribe = "nave/output/porta1";
const char *root_topic_publish = "nave/input/porta1";

//**************************************
//*********** WIFICONFIG ***************
//**************************************
const char* ssid = "MOVISTAR_9D42";
const char* password =  "PbTkYbKtMq66GPeXPGpH";



//**************************************
//*********** GLOBALES   ***************
//**************************************
WiFiClient espClient;
PubSubClient client(espClient);
char msg[25]; //mensaje voy enviar
long count=0;
const int pulsador = 27;
const int led = 25;
const int led_verde = 33;
int pulsado = 0;


//************************
//** F U N C I O N E S ***
//************************

//debemos declarar las funciones aquí, al principio

void callback(char* topic, byte* payload, unsigned int length);
void reconnect();
void setup_wifi();

void setup() {
  Serial.begin(115200);
  setup_wifi();

  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback); //cuando me llegue un mensaje ejecuta la funcion callback

  pinMode(pulsador, INPUT);
  pinMode(led, OUTPUT);
  pinMode(led_verde, OUTPUT);
  
}

void loop() {
  
  if (!client.connected()) {
    reconnect();
  }

  if (client.connected()){
    //String str = "La cuenta es -> " + String(count);
  
  pulsado = digitalRead(pulsador);
  if (pulsado == 1){
    Serial.println();
    Serial.print("Pulsador pulsado.");
    Serial.println();
    String str = "PULSADOR -> " + String(pulsado);
    digitalWrite(led,pulsado);
    str.toCharArray(msg,25);
    client.publish(root_topic_publish,msg);
    pulsado = 0;
    delay(2000);
    digitalWrite(led,pulsado);
  }
  
  }
  client.loop();
}




//*****************************
//***    CONEXION WIFI      ***
//*****************************
void setup_wifi(){
  delay(10);
  // Nos conectamos a nuestra red Wifi
  Serial.println();
  Serial.print("Conectando a ssid: ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("Conectado a red WiFi!");
  Serial.println("Dirección IP: ");
  Serial.println(WiFi.localIP());
}



//*****************************
//***    CONEXION MQTT      ***
//*****************************

void reconnect() {

  while (!client.connected()) {
    Serial.print("Intentando conexión Mqtt...");
    // Creamos un cliente ID
    String clientId = "esp32_daniel";
    clientId += String(random(0xffff), HEX);
    // Intentamos conectar
    if (client.connect(clientId.c_str(),mqtt_user,mqtt_pass)) {
      Serial.println("Conectado!");
      // Nos suscribimos
      if(client.subscribe(root_topic_subscribe)){
        Serial.println("Suscripcion ok");
      }else{
        Serial.println("fallo Suscripciión");
      }
    } else {
      Serial.print("fallo, con error -> ");
      Serial.print(client.state());
      Serial.println(" Intentamos de novo en 5 segundos");
      delay(5000);
    }
  }
}


//*****************************
//***       CALLBACK        ***
//*****************************

void callback(char* topic, byte* payload, unsigned int length){
  String incoming = "";
  Serial.print("Mensaje recibido desde -> ");
  Serial.print(topic);
  Serial.println("");
  for (int i = 0; i < length; i++) {
    incoming += (char)payload[i];
  }
  incoming.trim();
  Serial.println("Mensaje -> " + incoming);

  digitalWrite(led_verde,1);
  delay(2000);
  digitalWrite(led_verde,0);

}
