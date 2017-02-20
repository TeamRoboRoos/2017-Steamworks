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
int r0 = 4;
int r1 = 5;
int l0 = 2;
int l1 = 3;
int c0 = 6;

// Pins for pixy input
int in0 = 7;

// Tracking how many frames have gone though
int frameCount = 0;
int frameLast  = 0;
int frameDelay = 50;

//the amount you screwed up
int severity=0;

// Numnber of frames since we last got only two blocks
int framesWithMoreThanTwoBlocksOrLessThanOneBlock = 0;

// ---------------------------------------------------------------------------------------
//                                       Neopixel Variables

// Pin out for Neo Pixies
#define PIN 6

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
  pinMode(c0,OUTPUT);

  pinMode(in0,INPUT);
 
  // Neo Pixel set up
  strip.begin();
  solid(0,0,255);
  strip.show(); // Initialize all pixels to 'off'

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
 //if (digitalRead(in0) == HIGH) {
 //   partyMode = false;
 // }
 // else {
    //checkPixy();
 // }

 

  if (digitalRead(spikePin) == LOW) { 
    Serial.println("LOW");
    state = 4; 
    digitalWrite(spikePin, HIGH);
  } 
  else { 
    Serial.println("HIGH");
    if (state == 4 && digitalRead(spikePin) == HIGH) {
      state = 0;
    }
    
    checkPixy(); 
  }

  if (oldState != state) {
    solid(0,0,0);
    oldState = state;
  }
  
  long currentTime = millis();
  bool didChange = false;

 // Serial.print(state);
  
  
  if (currentTime >= lastBackChange + backDelay) {
    if (state == 0) { solid(255, 255, 0); }
    else if (state == 1) { right(); }
    else if (state == 2) { left(); }
    else if (state == 3) { forward();  }
    else if (state == 4) { solid(255, 0, 0); }
    else if (state == 5) { solid(0, 255, 255); }
    else { party(); backDelay = 150; partyMode = true; }
    lastBackChange = currentTime;
    didChange = true;
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

  if (didChange) {
    strip.show();
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

    // It may be worth checking that there are only two blocks. If there are more, stay on 
    // the previous inStruction. If it happens too much, stop.
    //if (blocks > 0 && blocks < 3)
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
      
      Serial.println(blocks);
      Serial.println(distanceApart);

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
            Serial.println(" right");
            state = 1;
            switch (severity)
            {
              case 3:                   // Hard
                digitalWrite(l0,HIGH);
                digitalWrite(l1,HIGH);
                digitalWrite(c0,LOW);
                digitalWrite(r0,LOW);
                digitalWrite(r1,LOW);
                break;
              case 2:                   // Moderate
                digitalWrite(l0,HIGH);
                digitalWrite(l1,LOW);
                digitalWrite(c0,LOW);
                digitalWrite(r0,LOW);
                digitalWrite(r1,LOW);
                break;
              default:                  // Puny
                digitalWrite(l0,LOW);
                digitalWrite(l1,HIGH);
                digitalWrite(c0,LOW);
                digitalWrite(r0,LOW);
                digitalWrite(r1,LOW);
            }
          }
          
          // Check if we need to turn right
          else
          {
            Serial.println(" left");
            state = 2;
            switch (severity)
            {
              case 3:                   // Hard
                digitalWrite(l0,LOW);
                digitalWrite(l1,LOW);
                digitalWrite(c0,LOW);
                digitalWrite(r0,HIGH);
                digitalWrite(r1,HIGH);
                break;
              case 2:                   // Moderate
                digitalWrite(l0,LOW);
                digitalWrite(l1,LOW);
                digitalWrite(c0,LOW);
                digitalWrite(r0,HIGH);
                digitalWrite(r1,LOW);
                break;
              default:                  // Puny
                digitalWrite(l0,LOW);
                digitalWrite(l1,LOW);
                digitalWrite(c0,LOW);
                digitalWrite(r0,LOW);
                digitalWrite(r1,HIGH);
                break;
            }
          }
        }

        // Driving straight
        else
        {
          Serial.println(" forward");
          state = 3;
          // Later this should incorporate range
          digitalWrite(l0,HIGH);
          digitalWrite(l1,LOW);
          digitalWrite(c0,LOW);
          digitalWrite(r0,HIGH);
          digitalWrite(r1,LOW);
        }
      }

      //  Lost signal
      else
      {
         Serial.println(" stop");
         state = 5;
         digitalWrite(l0,LOW);
         digitalWrite(l1,LOW);
         digitalWrite(c0,HIGH);
         digitalWrite(r0,LOW);
         digitalWrite(r1,LOW);
      }
    }

    // Lost block count - check to see if we need to shut down
    else {
      framesWithMoreThanTwoBlocksOrLessThanOneBlock++;
      Serial.println("***");
      if (framesWithMoreThanTwoBlocksOrLessThanOneBlock > 5) {
        //Serial.println("Lost lock");
        state = 5;
        digitalWrite(l0,LOW);
        digitalWrite(l1,LOW);
        digitalWrite(c0,LOW);
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
void solid(byte red, byte green, byte blue) {
  for (int i = 0; i < 15; i = i + 1) {
    strip.setPixelColor(i, red, green, blue);
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
    strip.setPixelColor(i, random(255), random(255), random(255));
  }
}





