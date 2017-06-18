void LedStrip::solidColor(int r, int g, int b) {
  for (int i = 0; i + this->from < this->to + 1; i++) { //steps through each pixel on the strip
    this->obj.setPixelColor(i + this->from, r, g, b); //and paints them in the color you told it to.
  }
  this->obj.show(); //after all pixels have been told what to do, the strip displays the changes.
}

void LedStrip::fade(int time) {
  static unsigned long timer = millis(); // creates a variable equal to the current time in milliseconds that will last between different callings of the function.

  float position = (millis() - timer) / (float)(2 * time); //creates a variable equaling how far we are in the sequence, i.e. .66 means we are 66% through the animation.

  while (position >= 1) {
    position --; //decrements position so that the value is always between 1 and 0.
  }

  if (position < .5) {
    this->solidColor(int (r * 2 * position), int (g * 2 * position), int (b * 2 * position)); // lights the strip, dimmed based on the value of position.
  } else {
    this->solidColor(int (r * 2 * (1 - position)), int (g * 2 * (1 - position)), int (b * 2 * (1 - position))); // same as previous, but now increasing position decreases the brightness.
  }
}

void LedStrip::blink(int wait) {
  static unsigned long timer = millis(); //creates a variable that will last between callings of the function equal to the time at the start of calling it.

  unsigned int f = (millis() - timer) / wait; // determines which frame of the animation we are on.
  f %= 2; //modulates f, so that the animation will reset when f >= 2

  if (f == 1) { //tests f. if f is equal to 1, the code in the block will run.
    this->solidColor(r, g, b); //paints the strip a solidColor based on what was passed into the function.
  } else {
    this->solidColor(0, 0, 0); //makes the lights turn off. Every light side needs a good dark side.
  }
}

void LedStrip::carnival(int wait) {
  static unsigned long timer = millis();
  unsigned int f = (millis() - timer) / wait;
  int c = f % 3;
  for (int i = 0; i < this->numPixels() + 1; i += 3) {
    this->obj.setPixelColor(i + c + this->from, this->obj.Color(r, g, b));
    if (i + c - 1 >= 0) {
      this->obj.setPixelColor(i + c - 1 + this->from, this->obj.Color(0, 0, 0));
    }
  }
}

void LedStrip::ripple(byte magnitude, byte wait)
{
  static unsigned long timer = millis();
  unsigned int f = (millis() - timer) / wait;

  int s = ((f % (magnitude * 2)) - (magnitude * 2) + 2);

  for (int p = 0; p < this->numPixels() + (2 * magnitude) - 2; p += (2 * magnitude)) {
    for (int i = 0; i < magnitude; i++) {
      if ((p + i - s) >= 0 && p + i - s < this->numPixels()) {
        this->obj.setPixelColor(p + i - s + this->from, this->obj.Color((i * r) / magnitude, (i * g) / magnitude, (i * b) / magnitude));
      }
    }
    for (int i = magnitude; i > 0; i--) {
      if ((p - s + ((magnitude * 2) - i)) >= 0 && (p - s + ((magnitude * 2) - i)) < this->numPixels()) {
        this->obj.setPixelColor((p - s + ((magnitude * 2) - i)) + this->from, this->obj.Color((i * r) / magnitude, (i * g) / magnitude, (i * b) / magnitude));
      }
    }
  }
}

void LedStrip::rainbowCycle(byte brightness, int wait) {
  static unsigned long timer = millis();
  unsigned f = (millis() - timer) / wait;
  int s = f % 256;

  for (int i; i + this->from < this->to + 1; i++) {
    this->obj.setPixelColor(i + this->from, wheelBrightness(((i * 256 / this->numPixels()) + s) & 255, brightness));
  }
}

void LedStrip::rainbowRandom(byte brightness, int wait) {
  if (millis() >= lastTime + wait) {
    for (int i; i + this->from < this->to + 1; i++) {
      int randint = random(255);
      this->obj.setPixelColor(i + this->from, wheelBrightness(randint, brightness));
      lastTime = millis();
    }
  }
}
