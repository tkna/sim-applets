# Gas
Java applet for gas simulation using kinetic theory of gas (created in 2009).

Blue lines in the graphs show the theoretical values derived from the gas state equations, and the red lines show the measured(simulated) values.

https://user-images.githubusercontent.com/69493688/147672084-e19e83d6-004a-43f9-8d19-d6fa415a7c9c.mp4


## Environment
Tested on JDK 1.8.0_311(64bit), Windows 10.

## Getting started

Execute the following in the directory above "gas".

```
javac -encoding UTF-8 Gas.java lib\*.java -d ./gas
appletviewer gas\Gas.java
```
