enum class AnimationType { //enums let us give names to different states.
  blackout,
  solidColor,
  blink,
  fade,
  carnival,
  ripple,
  rainbowCycle,
  rainbowRandom
}; //Creates a list of our animations.

static AnimationType animations[] = {
  AnimationType::blackout,      //0
  AnimationType::solidColor,    //1
  AnimationType::blink,         //2
  AnimationType::fade,          //3
  AnimationType::carnival,      //4
  AnimationType::ripple,        //5
  AnimationType::rainbowCycle,  //6
  AnimationType::rainbowRandom  //7
};

class LedStrip { //A class is a group of other data and functions.
  private:
    int r = 255, g = 255, b = 255;
    int from, to;
    Adafruit_NeoPixel obj;
    AnimationType currentAnimation = animations[3];
    long lastTime = 0; //Used in animations
  protected: // protected members can only be accessed by this class or other classes that inherit it.
  public:
    LedStrip(Adafruit_NeoPixel t_obj, int t_from, int t_to); //this function sets up the led strip accoring to the NeoPixel library.
    int numPixels();
    void setRGB(int r, int g, int b);
    void switchAnimation(AnimationType animation); //function to change cirrent animation on strip portion
    void animate(); //function to cycle the animation
    void solidColor(int r, int g, int b); // these are functions for animations.
    void fade(int wavelength);
    void blink(int wait);
    void carnival(int wait);
    void ripple(byte magnitude, byte wait);
    void rainbowCycle(byte brightness, int wait);
    void rainbowRandom(byte brightness, int wait);
};

char incoming1, incoming2, incoming3; // 3 single characters. will store the information on the serial port to be processed.
int sel, sel2;
