unsigned long timer;
String msg;


void setup() {
  Serial.begin(9600);
  pinMode(LED_BUILTIN, OUTPUT);
  while (!Serial) {}
}

void loop() {
  if (Serial.available() > 0) {
    msg = "Received: " + Serial.readString();
    digitalWrite(LED_BUILTIN, HIGH);
    
    timer = millis();
    while(timer + 1000 >= millis()) {}

    digitalWrite(LED_BUILTIN, LOW);
    for (int i=0; i < msg.length(); i++) {
      Serial.write(msg[i]);
    }
  }
}
