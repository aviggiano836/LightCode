#include <Adafruit_NeoPixel.h>
      #define PIN 7
//Parameter 1 = number of pixels
//Parameter 2 = pin number
//Parameter 3 = pixel type flags
Adafruit_NeoPixel strip = Adafruit_NeoPixel(2, PIN, NEO_GRB + NEO_KHZ800);
void setup() {
  strip.begin();
  strip.show();
}

void loop() {
//YOUR CODE
}
}
