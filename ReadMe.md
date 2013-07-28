#Description
A simple java library to convert Lambert Coordinates to GPS WGS84 coordinates based on the [IGN alorithms and methods](http://geodesie.ign.fr/contenu/fichiers/documentation/algorithmes/notice/NTG_71.pdf)

# Install
## From source with gradle
* Install `gradle`
* Compile with `gradle assemble`
* Add the generated `build/libs/lambert-java.jar` file to your project

## Bintray
* https://bintray.com/yageek/maven/lambert-java

# Usage

```java

 LambertPoint pt = Lambert.convertToWGS84Deg(994272.661, 113467.422, LambertZone.LambertI);
 System.out.println("Point latitude:" + pt.getY() + " longitude:" + pt.getX());
```

#License
Copyright (c) 2013 Yannick Heinrich - Released under the GPLv2 License.

