#Purpose:
convert pixels in an image with a specific RGB value to another RBGA value.

#Contents:
stand alone image converter (ColorSeparation.java)
bulk image converter base (RecolorerManager.java)

#Directions:
-configure `RecolorerManager` as you see fit (if people actually download this, i might consider making exaples for you).
-move java files to directory of target images
-in cmd, run `javac *.java` or `javac ColorSeparation.java RecolorerMangager.java`
-if RecolorerManager is not configured, run `java ColorSeparation <InputImageName.ext> <OutputImageName> <Outputext> <targetR> <targetG> <targetB> <convertR> <convertG> <convertB> <convertA>`
-if RecolorerManager is configured (by default it will turn the test image white) run `java RecolorerManager`