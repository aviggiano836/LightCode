#include <Adafruit_NeoPixel.h>
      #define PIN 3
//Parameter 1 = number of pixels
//Parameter 2 = pin number
//Parameter 3 = pixel type flags
Adafruit_NeoPixel strip = Adafruit_NeoPixel(20, PIN, NEO_GRB + NEO_KHZ800);
void setup() {
  strip.begin();
  strip.show();
}

void loop() {
//YOUR CODE
strip.setBrightness(45);
strip.setPixelColor(1, 200, 25, 160);
strip.show();
}
}
