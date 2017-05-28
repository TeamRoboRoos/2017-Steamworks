#include <Adafruit_NeoPixel.h> //adds the neopixel library to the code. This library handles the data going to the strip.

enum class AnimationType { //enums let us give names to different states.
  blackout,
  solidColor,
  blink,
  fade
}; //Creates a list of our animations.
//AnimationType animationID = AnimationType::solidColor; //creates animationID, which will store what animation we are running, and sets it to solidColor.

class LedStrip { //A class is a group of other data and functions.
  private:
    int from, to;
    Adafruit_NeoPixel obj;
    AnimationType currentAnimation;
  protected: // protected members can only be accessed by this class or other classes that inherit it.
  public:
    LedStrip(Adafruit_NeoPixel obj, int from, int to); //this function sets up the led strip accoring to the NeoPixel library.
    void switchAnimation(AnimationType animation); //function to change cirrent animation on strip portion
    void animate(); //function to cycle the animation
    void solidColor(int r, int g, int b); // these are functions for animations.
    void fade(int r, int g, int b, int wavelength);
    void blink(int r, int g, int b, int wait);
    //add more fun here
};

char incoming1, incoming2; // 2 single characters. will store the information on the serial port to be processed.

Adafruit_NeoPixel strip1 = Adafruit_NeoPixel(10, 3, NEO_GRB + NEO_KHZ800);
Adafruit_NeoPixel ring1 = Adafruit_NeoPixel(10, 5, NEO_GRB + NEO_KHZ800);

LedStrip stripTop = LedStrip(strip1, 0, 9);
LedStrip stripBottom = LedStrip(strip1, 10, 19);
LedStrip ringInside = LedStrip(ring1, 0, 9);
LedStrip ringOutside = LedStrip(ring1, 10, 19);


int red = 48; //global color values. Passed to most animations.
int green = 0; // these values go from 0 to 255, 0 being off and 255 being full power: Full Power is Very Bright!
int blue = 64; //the colors combine to create a single, RGB color.

//---------------------------------------------------------------

void setup() {
  strip1.begin();
  strip1.show();
  ring1.begin();
  ring1.show();

  Serial.begin(115200);
}

void loop() {
  if (Serial.available() > 1) {
    incoming1 = Serial.read();
    incoming2 = Serial.read();
    switch (incoming1) {

      case 's': //will run if incoming = 's'
        //animationID = AnimationType::solidColor;
        stripTop.switchAnimation(AnimationType::solidColor);
        stripBottom.switchAnimation(AnimationType::solidColor);
        ringInside.switchAnimation(AnimationType::solidColor);
        ringOutside.switchAnimation(AnimationType::solidColor);
        break; //ends the case.

      case 'o': //will run if incoming = 'o'
        stripTop.switchAnimation(AnimationType::blackout);
        stripBottom.switchAnimation(AnimationType::blackout);
        ringInside.switchAnimation(AnimationType::blackout);
        ringOutside.switchAnimation(AnimationType::blackout);
        break; //ends the case.

      case 'f': //will run if incoming = 'f'
        stripTop.switchAnimation(AnimationType::fade);
        stripBottom.switchAnimation(AnimationType::fade);
        ringInside.switchAnimation(AnimationType::fade);
        ringOutside.switchAnimation(AnimationType::fade);
        break; //ends the case.

      case 'b': //will run if incoming = 'b'
        stripTop.switchAnimation(AnimationType::blink);
        stripBottom.switchAnimation(AnimationType::blink);
        ringInside.switchAnimation(AnimationType::blink);
        ringOutside.switchAnimation(AnimationType::blink); //do not confuse with capital B, which stands for the color blue.
        break; //ends the case.

      case 'R': //will run if incoming = 'R'
        red = 64; //red alliance colors
        blue = 0;
        green = 0;
        break; //ends the case.

      case 'B': //will run if incoming = 'B'
        red = 0; //blue alliance colors
        green = 0;
        blue = 64;
        break; //ends the case.

      default: // will run if incoming matches none of the above.
        //you don't need a default, but it is here when wanted.
        break;

    }
  }

  stripTop.animate();
  stripBottom.animate();
  ringInside.animate();
  ringOutside.animate();
  strip1.show();
  ring1.show();
}

//---------------------------------------------------------------

LedStrip::LedStrip(Adafruit_NeoPixel obj, int from, int to) {
  obj = this->obj;
  from = this->from;
  to = this->to;
}

void LedStrip::switchAnimation(AnimationType animation) {
  currentAnimation = animation;
}

void LedStrip::animate() {
  switch (this->currentAnimation) {

    case AnimationType::blackout:
      this->solidColor(0, 0, 0);
      break;

    case AnimationType::solidColor:
      this->solidColor(red, green, blue);
      break;

    case AnimationType::fade:
      this->fade(red, green, blue, 2000);
      break;

    case AnimationType::blink:
      this->blink(red, green, blue, 500);
      break;

    default: // will run if incoming matches none of the above.
      //you don't need a default, but it is here when wanted.
      break;
  }
}

void LedStrip::solidColor(int r, int g, int b) {
  for (int i = 0; i + this->from < this->to + 1; i++) { //steps through each pixel on the strip
    this->obj.setPixelColor(i + this->from, r, g, b); //and paints them in the color you told it to.
  }
  this->obj.show(); //after all pixels have been told what to do, the strip displays the changes.
}

void LedStrip::fade(int r, int g, int b, int time) {
  static unsigned long timer = millis(); // creates a variable equal to the current time in milliseconds that will last between different callings of the function.

  float position = (millis() - timer) / (2 * time); //creates a variable equaling how far we are in the sequence, i.e. .66 means we are 66% through the animation.
  while (position >= 1) {
    position --; //decrements position so that the value is always between 1 and 0.
  }

  if (position < .5) {
    this->solidColor(int (r * 2 * position), int (g * 2 * position), int (b * 2 * position)); // lights the strip, dimmed based on the value of position.
  } else {
    this->solidColor(int (r * 2 * (1 - position)), int (g * 2 * (1 - position)), int (b * 2 * (1 - position))); // same as previous, but now increasing position decreases the brightness.
  }
}

void LedStrip::blink(int r, int g, int b, int wait) {
  static unsigned long timer = millis(); //creates a variable that will last between callings of the function equal to the time at the start of calling it.

  unsigned int f = (millis() - timer) / wait; // determines which frame of the animation we are on.
  f %= 2; //modulates f, so that the animation will reset when f >= 2

  if (f == 1) { //tests f. if f is equal to 1, the code in the block will run.
    this->solidColor(r, g, b); //paints the strip a solidColor based on what was passed into the function.
  } else {
    this->solidColor(0, 0, 0); //makes the lights turn off. Every light side needs a good dark side.
  }
}

