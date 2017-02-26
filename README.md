LightCode
=========

_Lighting the way to teach programming_


## About project
* [LightCode Website](https://aviggiano836.github.io/LightCode/)
* [WiCHacks 2017](http://wichacks.rit.edu/) project submission
* [DevPost project page](https://devpost.com/software/light-code)
* [See a demo](https://youtu.be/sbjNLAihfJ0)


## What is it?

We wanted to do something involving hardware and make something that would encourage people to learn how to code.

The end product for users is a battery-powered desk light with individually addressable RGB LEDs and sensors that the user learns to code to produce light matters based on the sensor's inputs. The users mix and match these lights for a string of colorful lights. The lights are covered in clear shapes (our prototypes are paper flowers).


## How we built it

For the programming side, we developed a simple [Scratch](https://scratch.mit.edu/)-like program for the users to code in to introduce them to coding in the [Arduino](https://www.arduino.cc/) language. This block code will be translated into the Arduino language and sent to the Arduino connected to the computer.

For the hardware/electronics, we prototyped our modular product using a 4-stranded cable and lots of header pins. We used an Arduino Uno to handle the software, making quick temporary electronic circuits using a breadboard and jumper wires. We used several RGB LEDs controlled by ws2811 driver chips and a photo-resistor that changes resistance when exposed to light.


## What's next

* Integrate with MIT Scratch or related Open Source projects such as Snap4Arduino
* Order custom PCBs and 3D print to work towards a more polished final product
* Create an online presence with lots of tutorial type things


## License

GPLv3
