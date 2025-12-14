#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <WiFi.h>
#include <PubSubClient.h>
#include "time.h"

// C++ code

//wifi
//const char* ssid = "MOVISTAR_0C33"; //cambiar para wifi OSCAR
//const char* password = "Z0023F8BBA6FA00"; //cambiar para wifi OSCAR

const char* ssid = "Xiaomi 14T Pro"; //cambiar para wifi MARTA
const char* password = "dianaespreciosa"; //cambiar para wifi MARTA 



//conexion a servidor mqtt de ubicua
const char* mqtt_server = "10.31.176.113";
const int mqtt_port = 1883;
const char* mqtt_user = "martayoscar";
const char* mqtt_password = "contador";
const char* client_id = "ESP32OscarMarta"; // ID unico

//para tener hora real
const char* ntpServer = "pool.ntp.org";
const long  gmtOffset_sec = 3600;  // 1 hora (CET)
const int   daylightOffset_sec = 3600;

//objetos
WiFiClient espClient;  //para la conexion
PubSubClient client(espClient);  //para el protocolo mqtt


//temas para suscribirse y publicar
const char* topic_publish = "sensors/ST_1678/traffic_counter";
const char* topic_subscribe = "sensors/ST_1678/#";


void setup_wifi();
void reconnect();
void callback(char* topic, byte* payload, unsigned int length);
void publicar_datos();


//Presión + num_mediciones
#define FORCE_PIN 35
double sensor = 0.0;
int contador;
String tipo_vehiculo_presion;
String tipo_vehiculo_distancia;
unsigned long StartTime;
unsigned long CurrentTime;

int valores_presion[5]={0,0,0,0,0};
int sumador = 0;
int media = 0;
int media_ts1=0;

//Parte Ultrasónico
int trigPin = 12;
int echoPin = 13;
long timer;
float distancia;




//Parte pantalla
LiquidCrystal_I2C lcd(0x27, 16, 2);

int contador_bicicletas=0;
int contador_coches=0;
int contador_camiones =0;
int contador_GAS=0;
int contador_ECO=0;

String matricula="";


//Parte sonido
int pinMicrofono = 5;
bool ha_sonado;


unsigned long ultMensaje = 0;


//vamos a establecer como threshold
//camion = >=4Newton y al tener más de dos ejes si >2 mediciones/0,5 segundo
//coche = >=2 y <4N Newton al tener solo dos ejes ==2 mediciones/0,5 segundo
//bici = <2Newton <2 mediciones/0,5 segundo

void setup()
{
  pinMode(2, OUTPUT);
  //Presión + num_mediciones
  Serial.begin(9600);
  StartTime=millis();
  contador = 0;
  
  // 1. CONECTAR WIFI
  setup_wifi();

  // 2. CONFIGURAR MQTT
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);  //función que recibe mensajes
  client.setBufferSize(1024);
  client.setKeepAlive(60);

  // 3. CONECTAR MQTT
  reconnect();

  // h
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
  Serial.print("Esperando hora NTP...");
  while(!time(nullptr)){
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nHora NTP sincronizada.");


  //Ultrasónico
  pinMode(trigPin, OUTPUT); // SETTING OUTPUT PIN
  pinMode(echoPin, INPUT); // SETTING INPUT PIN
  

  
  //Pantalla Led
  // Initialize the LCD
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0,0);
  lcd.print("hello world!");

  //Parte micrófono
  pinMode(pinMicrofono, INPUT);
}

  

void loop()
{
  
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
/*
  unsigned long t = millis();
  if (t - ultMensaje > 5000) {
    ultMensaje = t;
    publicar_datos();
  }
*/
  //Parte ultrasonidos
  digitalWrite(trigPin,LOW);
  delayMicroseconds(2);
  
  // transmitting sound for 10 microseconds
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(10, LOW);


  // calculating distance
  timer=pulseIn(echoPin , HIGH);
  distancia = timer * 0.0343/2;
  Serial.print("Distancia = " );
  Serial.println(distancia);
  
  
  if (distancia <=10){
    tipo_vehiculo_distancia="Camion";
  }
  else if (10<distancia && distancia<=15){
    tipo_vehiculo_distancia="Coche";
  }
  else{
    tipo_vehiculo_distancia="Bicicleta";
  }
      
  // Printing out the final output => distance
  Serial.print("Sensor Distancia = ");
  Serial.println(tipo_vehiculo_distancia);
  
  //Parte Sonido
  digitalWrite(pinMicrofono,LOW);
  delayMicroseconds(2);
  int sonido = digitalRead(pinMicrofono);

  if (!ha_sonado){
    if (sonido == HIGH)
    {
      ha_sonado=true;
      Serial.println("HA SONADO");
      
    }
    else{
      ha_sonado=false;
      
    }
  }
  
  digitalWrite(pinMicrofono,LOW);
  delayMicroseconds(2);
  
  
  
  //Parte presión + num ejes
  
  sensor = analogRead(FORCE_PIN);
  Serial.print("presion = " );
  Serial.println(sensor);
  CurrentTime=millis();
  
  /*
  Ahora mismo como no hay ningún objeto que pase por el sensor
  Está cogiendo más de dos presiones y por eso siempre sale camión
  Una vez se mida de manera que algo si pase por encima entonces se
  tendrá una medida más accurrate, aunque como lo haremos con juguetes
  poner un umbral de medición mayor a 0,5 segundos para que lo podamos medir bien
  en clase
  */
  
  if (sensor > 0) 
  {
    if (sensor>=100){
      valores_presion[contador]=sensor;
      contador ++;
    }
    
  }
    
  if (CurrentTime -StartTime >5000){
    
    if (contador==0){ 
      Serial.print("sensor presion = ");
      Serial.println("Nada");
    }
    else{
      for (int i=0;i<contador;i++){
        sumador+=valores_presion[i];
      }
      media=sumador/contador;

      if (contador==2){
        if (media<=500) { //if (sensor < 219 && contador<2){
          tipo_vehiculo_presion = "Bicicleta" ;
        }
        else{ //((sensor>=219 && sensor <314) && (contador==2))
          tipo_vehiculo_presion = "Coche";
        }
      }
      else if (contador>2){
        tipo_vehiculo_presion = "Camion";
      }
      Serial.print("sensor presion = ");
      Serial.println(tipo_vehiculo_presion);
      media_ts1=media;
    }
    contador=0;
    StartTime=millis();
    sumador=0;
    media=0;
    
    if (tipo_vehiculo_presion==tipo_vehiculo_distancia){

      matricula = "";
      // Primeros 4 números
      for (int i = 0; i < 4; i++) {
        matricula += String(random(0, 10));
      }
      // Últimas 3 letras en mayúscula
      for (int i = 0; i < 3; i++) {
        matricula += char(random(65, 91)); // ASCII 65-90 = A-Z
      }


      if (ha_sonado){
        contador_GAS++;
      }
      else{
        contador_ECO++;
      }
      lcd.setCursor(0,0);
      lcd.clear();
      lcd.print(tipo_vehiculo_presion);
      if (tipo_vehiculo_presion=="Bicicleta"){
        contador_bicicletas++;
      
      }
      else if (tipo_vehiculo_presion =="Coche"){
        contador_coches++;
      }
      else{
        contador_camiones++;
      }
      //contador contaminación
      lcd.setCursor(0,1);
      lcd.print("ECO=");
      lcd.print(contador_ECO);
      lcd.setCursor(7, 1);
      lcd.print("GAS=");
      lcd.print(contador_GAS);
      

      //contador tipo_vehículo
      lcd.setCursor(0,2);
      lcd.print("B=");
      lcd.print(contador_bicicletas);
      lcd.setCursor(5, 2);
      lcd.print("A=");
      lcd.print(contador_coches);
      lcd.setCursor(10, 2);
      lcd.print("C=");
      lcd.print(contador_camiones);

      publicar_datos();

    }
    else{
      lcd.setCursor(0,0);
      lcd.clear();
      lcd.print("Error x-)");

      
      

    }
    tipo_vehiculo_presion="Nada";
    ha_sonado=false;
    
    
  }
  delay(1000);
}

//conexion a wifi
void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Conectando a WiFi: ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  int intentos = 0;
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
    intentos++;
    
    if (intentos > 40) {
      Serial.println("\n No se pudo conectar");
      Serial.println("Reiniciando...");
      ESP.restart();
    }
  }

  Serial.println("\n WiFi conectado");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
}


//conexion a mqtt
 void reconnect() {
  while (!client.connected()) {
    Serial.print("Conectando a MQTT...");
    
    boolean conectado;
    conectado = client.connect(client_id, mqtt_user, mqtt_password);
    
    
    if (conectado) {
      Serial.println(" Conectado");
    } else {
      Serial.print(" Error rc=");
      Serial.println(client.state());
      Serial.println("Reintentando en 5s...");
      delay(5000);
      // Códigos de error:
      // -4 : timeout
      // -3 : conexión perdida
      // -2 : fallo al conectar
      // -1 : desconectado
      //  0 : conectado
      //  1 : protocolo incorrecto
      //  2 : ID rechazado
      //  3 : servidor no disponible
      //  4 : credenciales incorrectas
      //  5 : no autorizado
    }
  }
  client.subscribe(topic_subscribe);
}


void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Mensaje recibido en [");
  Serial.print(topic);
  Serial.print("]: ");
  
  String mensaje = "";
  for (int i = 0; i < length; i++) {
    mensaje += (char)payload[i];
  }
  Serial.println(mensaje);
  
  if (String(topic) == "sensors/ST_1678/cmd") {
    procesarComando(mensaje);
  }
}


void publicar_datos() {
  int total_vehiculos = contador_coches + contador_camiones;
  String currentTimestamp = getCurrentTimestamp();

  String tipo_emision_actual;
  if (ha_sonado) {
    tipo_emision_actual = "GAS";
  } else {
    tipo_emision_actual = "ECO";
  }

  // Crear JSON con los datos reales de ST_1678 - Calle del Sol
  String mensaje = "{";
  mensaje += "\"sensor_id\":\"TC_1678\",";
  mensaje += "\"sensor_type\":\"traffic_counter\",";
  mensaje += "\"street_id\":\"ST_1678\",";
  mensaje += "\"timestamp\":\"" + currentTimestamp + "\",";
  
  // Ubicación real de Calle del Sol
  mensaje += "\"location\":{";
  mensaje += "\"latitude\":40.4256518,";
  mensaje += "\"longitude\":-3.6619545,";
  mensaje += "\"district\":\"Salamanca\",";
  mensaje += "\"neighborhood\":\"Parque de las Avenidas\",";
  mensaje += "\"postal_code\":\"28009\",";
  mensaje += "\"street_name\":\"Calle del Sol\",";
  mensaje += "\"street_length\":63.21,";
  mensaje += "\"surface_type\":\"asphalt\",";
  mensaje += "\"speed_limit\":30";
  mensaje += "},";
  
  // Datos del sensor
  mensaje += "\"data\":{";
  mensaje += "\"vehicle_count\":" + String(total_vehiculos) + ",";
  mensaje += "\"pedestrian_count\":0,";
  mensaje += "\"bicycle_count\":" + String(contador_bicicletas) + ",";
  mensaje += "\"car_count\":" + String(contador_coches) + ",";
  mensaje += "\"truck_count\":" + String(contador_camiones) + ",";
  mensaje += "\"eco_count\":" + String(contador_ECO) + ",";
  mensaje += "\"gas_count\":" + String(contador_GAS) + ",";
  mensaje += "\"plate\":\"" + String(matricula) + "\",";
  mensaje += "\"mean_pressure\":" + String(media_ts1) + ",";
  mensaje += "\"distance\":" + String(distancia) + ",";
  mensaje += "\"type_vehicle\":\"" + String(tipo_vehiculo_presion) + "\",";
  mensaje += "\"direction\":\"north\",";
  mensaje += "\"counter_type\":\"vehicle\",";
  mensaje += "\"technology\":\"" + tipo_emision_actual + "\"";
  mensaje += "}";
  mensaje += "}";
  
  // Publicar
  if (client.publish(topic_publish, mensaje.c_str())) {
    Serial.println("Datos publicados - Calle del Sol (ST_1678)");
    Serial.println(mensaje);
  } else {
    Serial.println("Error al publicar");
  }
}



String getCurrentTimestamp() {
  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    return "2000-01-01 00:00:00"; // Devolver un valor por defecto si falla
  }
  
  // Formato SQL: YYYY-MM-DD HH:MM:SS
  char buffer[20];
  strftime(buffer, 20, "%Y-%m-%d %H:%M:%S", &timeinfo);
  return String(buffer);
}



void procesarComando(String json) {
  Serial.println("Procesando comando: " + json);
  
  // Comando: RESET
  if (json.indexOf("\"action\":\"reset\"") > 0) {
    Serial.println("Reseteando contadores...");
    
    contador_bicicletas = 0;
    contador_coches = 0;
    contador_camiones = 0;
    contador_ECO = 0;
    contador_GAS = 0;
    
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("RESET OK!");
    delay(2000);
    
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("ECO=0 GAS=0");
    lcd.setCursor(0,1);
    lcd.print("B=0 A=0 C=0");
    
    Serial.println("Contadores reseteados");
  }
  
}

