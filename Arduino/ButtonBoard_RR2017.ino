#include "Joystick.h"

// Create Joystick
Joystick_ Joystick;

// Set to true to test "Auto Send" mode or false to test "Manual Send" mode.
//const bool testAutoSendMode = true;
const int BUTTON1PIN = 2;
const int BUTTON2PIN = 3;
const int BUTTON3PIN = 4;
const int BUTTON4PIN = 5;
const int BUTTON5PIN = 6;

const int BUTTON1 = 0;
const int BUTTON2 = 1;
const int BUTTON3 = 2;
const int BUTTON4 = 3;
const int BUTTON5 = 4;

const int LED = 15;

const bool testAutoSendMode = false;

const unsigned long gcCycleDelta = 1000;
const unsigned long gcAnalogDelta = 25;
const unsigned long gcButtonDelta = 500;
unsigned long gNextTime = 0;
unsigned int gCurrentStep = 0;


void setup() {

  if (testAutoSendMode)
  {
    Joystick.begin();
  }
  else
  {
    Joystick.begin(false);
  }
  
  pinMode(A0, INPUT_PULLUP);

  pinMode(BUTTON1PIN, INPUT_PULLUP);
  pinMode(BUTTON2PIN, INPUT_PULLUP);
  pinMode(BUTTON3PIN, INPUT_PULLUP);
  pinMode(BUTTON4PIN, INPUT_PULLUP);
  pinMode(BUTTON5PIN, INPUT_PULLUP);
  
  pinMode(LED, OUTPUT);
}

void loop() {
  int flashLoop=0;
  
  // System Disabled
  //if (digitalRead(A0) != 0)
  //{
    // Turn indicator light off.
    //digitalWrite(LED, 0);
    //return;
  //}

  // Turn indicator light on.
  digitalWrite(LED, 1);
 
  if (digitalRead(BUTTON1PIN) == 0)
  {
    Joystick.pressButton(BUTTON1);
    flashLoop=1;
  }
  else
  {
    Joystick.releaseButton(BUTTON1);
  }
  
  if (digitalRead(BUTTON2PIN) == 0)
  {
    Joystick.pressButton(BUTTON2);
    flashLoop=2;
  }
  else
  {
    Joystick.releaseButton(BUTTON2);
  }
    
  if (digitalRead(BUTTON3PIN) == 0)
  {
    Joystick.pressButton(BUTTON3);
    flashLoop=3;
  }
  else
  {
    Joystick.releaseButton(BUTTON3);
  }
    
  if (digitalRead(BUTTON4PIN) == 0)
  {
    Joystick.pressButton(BUTTON4);
    flashLoop=4;
  }
  else
  {
    Joystick.releaseButton(BUTTON4);
  }

   
  if (digitalRead(BUTTON5PIN) == 0)
  {
    Joystick.pressButton(BUTTON5);
    flashLoop=5;
  }
  else
  {
    Joystick.releaseButton(BUTTON5);
  }

  
  if (testAutoSendMode == false)
  {
    Joystick.sendState();
  }
    
 
}
