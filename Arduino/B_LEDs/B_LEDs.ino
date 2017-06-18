#include <Adafruit_NeoPixel.h> //adds the neopixel library to the code. This library handles the data going to the strip.
#include "Headers.h"

static bool charMode = true; //Set to true to look for chars on serial instead of ints, excludes control character
static bool controlCheck = true; //Set to true to check to ignore CL/NL characters

//Physical strips attached to sepperate data pins
static Adafruit_NeoPixel ledObjs[] = {Adafruit_NeoPixel(15, 2, NEO_GRB + NEO_KHZ800), Adafruit_NeoPixel(15, 3, NEO_GRB + NEO_KHZ800)};
//Strip segments in software, they are a part of the above strips
static LedStrip ledStrips[] = {LedStrip(ledObjs[0], 0, 14), LedStrip(ledObjs[1], 0, 14)};//, LedStrip(ledObjs[1], 10, 14), LedStrip(ledObjs[0], 10, 14)};

static int numLedObjs = sizeof(ledObjs) / sizeof(ledObjs[0]); //Number of software objects, these are parts of physical strips
static int numLedStrips = sizeof(ledStrips) / sizeof(ledStrips[0]); //Number of physical strips
static int numAnimations = sizeof(animations) / sizeof(animations[0]); //Number of animations that can be run on the strips
//---------------------------------------------------------------

void setup() {
  for (int i = 0; i < numLedObjs; i++) {
    ledObjs[i].begin();
    ledObjs[i].show();
  }

  Serial.begin(9600);
  Serial.println("COMPLETE");
  randomSeed(analogRead(0));
}

void loop() {
  if (Serial.available() > 2) { //Check there are at least 3 characters in the buffer
    incoming1 = Serial.read();
    Serial.println(incoming1);
    if (incoming1 == '~') { //Check the first character is the control character
      incoming2 = Serial.read();
      if (incoming2 == 13 && controlCheck) {
        Serial.read();
        incoming2 = Serial.read();
      }
      Serial.println(incoming2);
      if (incoming2 != '~') { //Check the second character is not the control character
        incoming3 = Serial.read();
        if (incoming3 == 13 && controlCheck) {
          Serial.read();
          incoming3 = Serial.read();
        }
        Serial.println(incoming3);
        if (incoming3 != '~') { //Check the third character is not the control character

          if (charMode) {
            sel = incoming2 - '0'; //Make the character a diget
            sel2 = incoming3 - '0';
          }
          else {
            sel = incoming2;
            sel2 = incoming3;
          }

          if (sel2 <= 9) {
            ledStrips[sel].switchAnimation(animations[sel2]);
          }

          switch (incoming3) {
            case 'R': //will run if incoming = 'R'
              ledStrips[sel].setRGB(255, 0, 0);
              break; //ends the case.

            case 'G': //will rin if incoming = 'G'
              ledStrips[sel].setRGB(0, 255, 0);
              break; //ends the case.

            case 'B': //will run if incoming = 'B'
              ledStrips[sel].setRGB(0, 0, 255);
              break; //ends the case.

            case 'Y': //will run if incoming = 'Y'
              ledStrips[sel].setRGB(126, 84, 0);
              break; //ends the case.

            case 'O': //will run if incoming = 'O'
              ledStrips[sel].setRGB(158, 63, 0);
              break; //ends the case.

            case 'T': //will run if incoming = 'T'
              ledStrips[sel].setRGB(0, 127, 127);
              break; //ends the case.

            case 'V': //will run if incoming = 'V'
              ledStrips[sel].setRGB(127, 0, 127);
              break; //ends the case.

            case 'W': //will run if incoming = 'W'
              ledStrips[sel].setRGB(255, 255, 255);
              break; //ends the case.
          }
        }
      }
    }
  }

  for (int i = 0; i < numLedStrips; i++) {
    ledStrips[i].animate();
  }
  for (int i = 0; i < numLedObjs; i++) {
    ledObjs[i].show();
  }
}

//---------------------------------------------------------------

LedStrip::LedStrip(Adafruit_NeoPixel t_obj, int t_from, int t_to) {
  obj = t_obj;
  from = t_from;
  to = t_to;
}

int LedStrip::numPixels() {
  return (to - from) + 1;
}

void LedStrip::setRGB(int r, int g, int b) {
  this->r = r;
  this->g = g;
  this->b = b;
}

void LedStrip::switchAnimation(AnimationType animation) {
  this->currentAnimation = animation;
}

void LedStrip::animate() {
  switch (this->currentAnimation) {

    case AnimationType::blackout:
      this->solidColor(0, 0, 0);
      break;

    case AnimationType::solidColor:
      this->solidColor(this->r, this->g, this->b);
      break;

    case AnimationType::fade:
      this->fade(2000);
      break;

    case AnimationType::blink:
      this->blink(500);
      break;

    case AnimationType::carnival:
      this->carnival(80);
      break;

    case AnimationType::ripple:
      this->ripple(8, 25);
      break;

    case AnimationType::rainbowCycle:
      this->rainbowCycle(255, 10);
      break;

    case AnimationType::rainbowRandom:
      this->rainbowRandom(255, 75);
      break;
  }
}

uint32_t wheelBrightness(byte position, byte brightness) {
  if (position < 85) {
    return Adafruit_NeoPixel::Color((position * 3 * brightness) / 255, ((255 - position * 3) * brightness) / 255, 0);
  } else if (position < 170) {
    position -= 85;
    return Adafruit_NeoPixel::Color(((255 - position * 3) * brightness) / 255, 0, (position * 3 * brightness) / 255);
  } else {
    position -= 170;
    return Adafruit_NeoPixel::Color(0, (position * 3 * brightness) / 255, ((255 - position * 3) * brightness) / 255);
  }
}
