Para usar la app de Android desde un móvil en vez de el emulador, será necesario cambiar la ip del broker de mqtt, configurada como localhost, por la ipv4 del ordenador donde se ejecute el broker en estos dos archivos:
MqttMonitoringActivity.java:
private static final String MQTT_BROKER_URL = "tcp://10.0.2.2:1883";
RetrofitClient.java:
private static final String BASE_URL = "http://10.0.2.2:8080/";
