#include <SPI.h>
#include <Pixy.h>
#include <Adafruit_NeoPixel.h>

// ---------------------------------------------------------------------------------------
//                                          Pixy Variables

Pixy pixy;

// Middle of the pixy's screen
#define PIXY_MIDDLE 160
// Minimum range of the pixy - stop when the value goes above this
#define PIXY_RANGE 100
// Range for turning hard
#define PIXY_HARD 75
// Range for turning moderate
#define PIXY_MODERATE 50
// Range for turning a little bit
#define PIXY_PUNY 2

// Pins for pixy output
int r0 = 6;
int r1 = 7;
int l0 = 8;
int l1 = 9;

// Pins for pixy input
int in0 = 10;
int in1 = 11;

// Tracking how many frames have gone though
int frameCount = 0;
int frameLast  = 0;
int frameDelay = 50;

//the amount you screwed up
int severity=0;

// Slow distance
int slow = 1200;

// Medium distance
int medium = 600;


// Numnber of frames since we last got only two blocks
int framesWithMoreThanTwoBlocksOrLessThanOneBlock = 0;

// ---------------------------------------------------------------------------------------
//                                       Neopixel Variables

// Pin out for NeoPixel
#define PIN 5

// Parameter 1 = number of pixels in strip
// Parameter 2 = pin number (most are valid)
// Parameter 3 = pixel type flags, add together as needed:
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     Pixels are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     Pixels are wired for RGB bitstream (v1 FLORA pixels, not v2)
Adafruit_NeoPixel strip = Adafruit_NeoPixel(29, PIN, NEO_GRB + NEO_KHZ800);

int level = 0;

// Movement forward
int currentFowardLED = 0;

// Night Rider effect on front of robot
int frontLED[3];
int frontLEDDirection[3];

int frontDelay = 150;
long lastFrontChange = 0;

int frontSpeedDelay = 1000;
long lastFrontSpeedChange = 0;

int backDelay = 100;
long lastBackChange = 0;

int state = 0;
int oldState = 0;

bool partyMode = false;
bool setupMode = true;

bool debugOutput = false;

// ---------------------------------------------------------------------------------------
//                                          LED Light Ring

#define LIGHT_RING_PIN 3
#define LIGHT_RING_LEDS 40

#define LIGHT_RING_GREEN 0
#define LIGHT_RING_BLUE 1
#define LIGHT_RING_RED 2

#define LIGHT_RING 0
#define LIGHT_STRIPS 1

Adafruit_NeoPixel lightRing = Adafruit_NeoPixel(LIGHT_RING_LEDS, LIGHT_RING_PIN, NEO_GRB + NEO_KHZ800);

int const LIGHT_RING_DELAY = 1500;

long lastLightRingChange = 0;
bool lightRingState = LIGHT_RING_GREEN;

int numberOfFramesSinceLastLightRingColorChange = 0;
int missedFramesNeededToChangeColor = 50;

// ---------------------------------------------------------------------------------------
//                                          Spike Switch

#define spikePin 2

// ---------------------------------------------------------------------------------------
//                                             Setup

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pixy.init();

  pinMode(r0,OUTPUT);
  pinMode(r1,OUTPUT);
  pinMode(l0,OUTPUT);
  pinMode(l1,OUTPUT);

  //pinMode(in0,INPUT);
 // pinMode(in1,INPUT);
 
  // Neo Pixel set up
  strip.begin();
  solid(0,0,255, LIGHT_STRIPS);
  strip.show(); // Initialize all pixels to 'off'

  
  lightRing.begin();
  solid(0,0,255, LIGHT_RING);
  lightRing.show(); // Initialize all pixels to 'off'

  frontLED[0] = 17;
  frontLED[1] = 16;
  frontLED[2] = 15;
  
  frontLEDDirection[0] = 1;
  frontLEDDirection[1] = 1;
  frontLEDDirection[2] = 1;

  // Spike switch
  pinMode(spikePin, INPUT_PULLUP);
  
}


// ---------------------------------------------------------------------------------------
//                                         Main Loop

void loop() {
  // Check for debug purposes
  if (digitalRead(in0) == LOW) { debug("0 - LOW"); } 
  if (digitalRead(in0) == HIGH) { debug("0 - HIGH"); } 
  if (digitalRead(in1) == LOW) { debug("1 - LOW"); } 
  if (digitalRead(in1) == HIGH) { debug("1 - HIGH"); } 

  // Output for debug purposes
  //digitalWrite(7,LOW);
  //digitalWrite(8,LOW);
  //digitalWrite(9,LOW);
  //digitalWrite(10,HIGH);

  
/*
  if (digitalRead(in1) == HIGH) {
    backDelay = 150; 
    partyMode = true; 
  }
  
  if (partyMode && digitalRead(in0) == HIGH) {
    state = 0;
    partyMode = false; 
  }

  */
  if (setupMode) { setupModeLoop(); }
  if (partyMode) { partyModeLoop(); }
  else { competitionModeLoop(); }
}

void competitionModeLoop() {
  
  // Check to see if the spike has been hit

  if (digitalRead(spikePin) == LOW) { 
    state = 4; 
    digitalWrite(l0,HIGH);
    digitalWrite(l1,HIGH);
    digitalWrite(r0,HIGH);
    digitalWrite(r1,HIGH);
    digitalWrite(spikePin, HIGH);
    setupMode = false;
  } 
  
  // If it hasn't been hit, we check to see if we thought it had been hit, and reset it. 
  // Then we grab the pixy data
  
  else { 
    if (state == 4 && digitalRead(spikePin) == HIGH) {
      state = 0;
      digitalWrite(l0,LOW);
      digitalWrite(l1,LOW);;
      digitalWrite(r0,LOW);
      digitalWrite(r1,LOW);
    }

    checkPixy(); 
    debug("--------------------------------------");
  }

  // Check to see if we need to change state

  if (oldState != state) {
    solid(0,0,0, LIGHT_STRIPS);
    oldState = state;
  }
  
  long currentTime = millis();
  bool didChange = false;
  bool didChangeLightRing = false;

  if (currentTime >= lastBackChange + backDelay) {
    if (state == 0) { solid(255, 255, 0, LIGHT_STRIPS); }
    else if (state == 1) { right(); }
    else if (state == 2) { left(); }
    else if (state == 3) { forward();  }
    else if (state == 4) { solid(255, 0, 0, LIGHT_STRIPS); }
    else if (state == 5) { solid(0, 255, 255, LIGHT_STRIPS); }
    lastBackChange = currentTime;
    didChange = true;
    debug("*****************************");
    debug(state);
  }
  
  if (currentTime >= lastFrontChange + frontDelay && !partyMode) {
    frontLEDs(255,0,0);
    lastFrontChange = currentTime;
    didChange = true;
  }
  
  if (currentTime >= lastFrontSpeedChange + frontSpeedDelay) {
    frontDelay = frontDelay  - 1;
    lastFrontSpeedChange = currentTime;
    if (frontDelay < 30) frontDelay = 30;
  }

  if (numberOfFramesSinceLastLightRingColorChange > missedFramesNeededToChangeColor && !partyMode) {
    numberOfFramesSinceLastLightRingColorChange = 0;
    lightRingChangeColor();
    didChangeLightRing = true;
  }
  
  if (didChange) {
    strip.show();
  }

  if (didChangeLightRing) {
    lightRing.show();
  }
}

void setupModeLoop() {
  long currentTime = millis();
  bool didChange = false;
  bool didChangeLightRing = false;
  
  if (currentTime > lastLightRingChange + LIGHT_RING_DELAY) {
    
    lastLightRingChange = currentTime;

    lightRingChangeColor();

    didChangeLightRing = true;
  }

  if (currentTime >= lastFrontChange + frontDelay) {
    if (lightRingState == LIGHT_RING_BLUE) {
      frontLEDs(0,0,255);
    }
    else {
      frontLEDs(0,255,0);
    }
    lastFrontChange = currentTime;
    didChange = true;
  }

  if (didChange) {
    strip.show();
  }

  if (didChangeLightRing) {
    lightRing.show();
  }
}

void partyModeLoop() {
  long currentTime = millis();
  bool didChange = false;
  bool didChangeLightRing = false;
  
  if (currentTime >= lastBackChange + backDelay) {
    party();  
    didChangeLightRing = true;
    didChange = true;
  }
  
  if (didChange) {
    strip.show();
  }

  if (didChangeLightRing) {
    lightRing.show();
  }
}

// ---------------------------------------------------------------------------------------
//                                   Check the Pixy camera

void checkPixy() {
  // Check to see if it is time to grab a frame
  frameCount++;
  if (frameCount > frameLast + frameDelay)
  {
    // Yes! It is time to grab a frame! Yay!
    // Better record that first, though, so we can wait until we do this again.
    frameLast = frameCount;
  
    // Read pixy data 
    uint16_t blocks;

    // How many blocks can it see?
    blocks = pixy.getBlocks();

    debug("########################################");
    debug(blocks);
    debug("########################################");

    if (blocks)
    {
      // Reset missing blocks counter
      framesWithMoreThanTwoBlocksOrLessThanOneBlock = 0;

      // Calculate the mid point between the two blocks
      
      int distanceApart = abs(pixy.blocks[0].x - pixy.blocks[1].x);
      
      int midX = pixy.blocks[0].x - pixy.blocks[1].x;
      midX = abs(midX);
      midX = midX / 2;

      if (pixy.blocks[0].x < pixy.blocks[1].x) 
      { 
        midX = pixy.blocks[0].x  + midX; 
      } 
      else 
      { 
        midX = pixy.blocks[1].x  + midX; 
      }

      // Calculate how far to the left or right we are.

      int missingDistance = PIXY_MIDDLE - midX;     
      
      debug(blocks);
      debug(distanceApart);

      // Calulcate how far off we are (left or right - doesn't care)
      if (abs(missingDistance) < PIXY_RANGE) 
      {
        if (abs(missingDistance) > PIXY_HARD)
        {
          Serial.print("hard");
          severity = 3;
          backDelay = 50;
        }
        else if (abs(missingDistance) > PIXY_MODERATE)
        {
          Serial.print("moderate");
          severity = 2;  
          backDelay = 100;
        }
        else if (abs(missingDistance) > PIXY_PUNY)
        {
          Serial.print("puny");
          severity = 1;
          backDelay = 200;
        }
        else
        {
          Serial.print("straight");
          severity = 0;
          backDelay = 100;
        }

        if (severity > 0) 
        {
          // Check if we ned to turn right
          if (missingDistance > 0) 
          {
            debug(" right");
            state = 1;
            switch (severity)
            {
              case 3:                   // Hard
                digitalWrite(l0,HIGH);
                digitalWrite(l1,HIGH);
                digitalWrite(r0,LOW);
                digitalWrite(r1,LOW);
                break;
              case 2:                 // Moderate
                digitalWrite(l0,HIGH);
                digitalWrite(l1,LOW);;
                digitalWrite(r0,LOW);
                digitalWrite(r1,LOW);
                break;
              default:                  // Puny
                digitalWrite(l0,LOW);
                digitalWrite(l1,HIGH);
                digitalWrite(r0,LOW);
                digitalWrite(r1,LOW);
            }
          }
          
          // Check if we need to turn right
          else
          {
            debug(" left");
            state = 2;
            switch (severity)
            {
              case 3:                   // Hard
                digitalWrite(l0,LOW);
                digitalWrite(l1,LOW);
                digitalWrite(r0,HIGH);
                digitalWrite(r1,HIGH);
                break;
              case 2:                   // Moderate
                digitalWrite(l0,LOW);
                digitalWrite(l1,LOW);
                digitalWrite(r0,HIGH);
                digitalWrite(r1,LOW);
                break;
              default:                  // Puny
                digitalWrite(l0,LOW);
                digitalWrite(l1,LOW);
                digitalWrite(r0,LOW);
                digitalWrite(r1,HIGH);
                break;
            }
          }
        }

        // Driving straight
        else
        {
          debug(" forward");
          state = 3;
          // Later this should incorporate range
          digitalWrite(l0,HIGH);
          digitalWrite(l1,LOW);
          digitalWrite(r0,HIGH);
          digitalWrite(r1,LOW);
        }
      }

      //  Lost signal
      else
      {
         debug(" stop");
         state = 5;
         digitalWrite(l0,LOW);
         digitalWrite(l1,LOW);
         digitalWrite(r0,LOW);
         digitalWrite(r1,LOW);

         numberOfFramesSinceLastLightRingColorChange++;
      }
    }

    // Lost block count - check to see if we need to shut down
    
    else {
      framesWithMoreThanTwoBlocksOrLessThanOneBlock++;
      numberOfFramesSinceLastLightRingColorChange++;
      debug("***");
      if (framesWithMoreThanTwoBlocksOrLessThanOneBlock > 5) {
        //debug("Lost lock");
        state = 5;
        digitalWrite(l0,LOW);
        digitalWrite(l1,LOW);
        digitalWrite(r0,LOW);
        digitalWrite(r1,LOW);
      }
    }
  }
}

// ---------------------------------------------------------------------------------------
//                                   Function's to handle Neo Pixel

//Rear LED Strip

// Indicate drive forward
void forward() {
  if (currentFowardLED > 7) {
    currentFowardLED = 0;
    strip.setPixelColor(7, 0, 0, 0);
    strip.setPixelColor(6, 0, 0, 0);
    strip.setPixelColor(8, 0, 0, 0);
    strip.setPixelColor(5, 0, 0, 0);
    strip.setPixelColor(9, 0, 0, 0);
  }

  strip.setPixelColor(currentFowardLED - 3, 0, 0, 0);
  strip.setPixelColor(currentFowardLED - 2, 0, 15, 0);
  strip.setPixelColor(currentFowardLED - 1, 0, 63, 0);
  strip.setPixelColor(currentFowardLED, 0, 255, 0);
  if (14 - currentFowardLED + 3 < 15) strip.setPixelColor(14 - currentFowardLED + 3, 0, 0, 0);
  if (14 - currentFowardLED + 2 < 15) strip.setPixelColor(14 - currentFowardLED + 2, 0, 15, 0);
  if (14 - currentFowardLED + 1 < 15) strip.setPixelColor(14 - currentFowardLED + 1, 0, 63, 0);
  strip.setPixelColor(14 - currentFowardLED, 0, 255, 0);
  currentFowardLED = currentFowardLED + 1;
}

// Light up all one colour
void solid(byte red, byte green, byte blue, int target) {
  if (target == LIGHT_STRIPS) {
    for (int i = 0; i < 15; i = i + 1) {
      strip.setPixelColor(i, red, green, blue);
    }
  }
  
  if (target == LIGHT_RING) {
    for (int i = 0; i < LIGHT_RING_LEDS; i = i + 1) {
      lightRing.setPixelColor(i, red, green, blue);
    }
  }
}

// Turn right
void right() {
  if (currentFowardLED > 14) {
    currentFowardLED = 0;
    strip.setPixelColor(14, 0, 0, 0);
    strip.setPixelColor(13, 0, 0, 0);
    strip.setPixelColor(12, 0, 0, 0);
    strip.setPixelColor(11, 0, 0, 0);
  }

  strip.setPixelColor(currentFowardLED - 3, 0, 0, 0);
  strip.setPixelColor(currentFowardLED - 2, 15, 0, 15);
  strip.setPixelColor(currentFowardLED - 1, 63, 0, 63);
  strip.setPixelColor(currentFowardLED, 255, 0, 255);
  currentFowardLED = currentFowardLED + 1;
}

// Turn Left
void left() {
  if (currentFowardLED < 0) {
    currentFowardLED = 14;
    strip.setPixelColor(0, 0, 0, 0);
    strip.setPixelColor(1, 0, 0, 0);
    strip.setPixelColor(2, 0, 0, 0);
    strip.setPixelColor(3, 0, 0, 0);
  }

  if (currentFowardLED + 3 < 15) strip.setPixelColor(currentFowardLED + 3, 0, 0, 0);
  if (currentFowardLED + 2 < 15) strip.setPixelColor(currentFowardLED + 2, 0, 0, 15);
  if (currentFowardLED + 1 < 15) strip.setPixelColor(currentFowardLED + 1, 0, 0, 63);
  strip.setPixelColor(currentFowardLED, 0, 0, 255);
  currentFowardLED = currentFowardLED - 1;
}

// Front LEDs
void frontLEDs(byte red, byte green, byte blue) {  
  for (int i = 15; i < 29; i++) {
    strip.setPixelColor(i, red / 8, green / 8, blue / 8);
  }
  
  for (int i = 0; i < 3; i++) {
    frontLED[i] += frontLEDDirection[i];
    if (frontLED[i] == 28) frontLEDDirection[i] = -1;
    if (frontLED[i] == 15) frontLEDDirection[i] = 1;
  }
  
  strip.setPixelColor(frontLED[0], red, green, blue);
  strip.setPixelColor(frontLED[1], red / 4, green / 4, blue / 4);
  strip.setPixelColor(frontLED[2], red / 8, green / 8, blue / 8);
}

// Party Mode
void party() {

  for (int i = 0; i < 29; i = i + 1) {
    int red = random(255);
    int green = random(255);
    int blue = random(255);

    int black = random(3);
    if (black == 0) { red = 0; }
    if (black == 1) { green = 0; }
    if (black == 2) { blue = 0; }
    strip.setPixelColor(i, red, green, blue);
  }
  
  for (int i = 0; i < 40; i = i + 1) {
    int red = random(128);
    int green = random(128);
    int blue = random(128);

    int black = random(3);
    if (black == 0) { red = 0; }
    if (black == 1) { green = 0; }
    if (black == 2) { blue = 0; }
  
    lightRing.setPixelColor(i, red, green, blue);
  }
}


void lightRingChangeColor() {      
  if (lightRingState == LIGHT_RING_GREEN) {
    // Change to blue
    solid(0,0,255, LIGHT_RING);
    lightRingState = LIGHT_RING_BLUE;
  }

  else {
    // Change to green
    solid(0,255,0, LIGHT_RING);
    lightRingState = LIGHT_RING_GREEN;
  }
}


// ------------------------------------------------------------------------------

void debug(String out) {
  if (debugOutput) {
    Serial.println(out);
  }
}
void debug(int out) {
  if (debugOutput) {
    Serial.println(out);
  }
}


