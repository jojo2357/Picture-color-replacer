# Purpose: 
* convert pixels in an image with a specific RGB value to another RBGA value.

# Notes:
* Converting between files IS supported, although i havent tested every single possiblity, but do let me know what conversions are not applicable.
* If you just want to convert file type, make all the RGB numbers the same and do not put an alpha value. Make the 

# Contents: 
* stand alone image converter (ColorSeparation.java)
* bulk image converter base (RecolorerManager.java)

# Demo Directions:

## Windows:
* run RunManager.bat

## Other:
* I gave u a bash script i think so good luck with that (I use windows lol)

# Directions: 
* configure `RecolorerManager` as you see fit (if people actually download this, i might consider making exaples for you). 
* move java files to directory of target images 
* in cmd, run `javac *.java` or `javac ColorSeparation.java RecolorerMangager.java` 
* if RecolorerManager is not configured, run `java ColorSeparation <InputImageName.ext> <OutputImageName> <Outputext> <targetR> <targetG> <targetB> <convertR> <convertG> <convertB > <convertA>` 
* if RecolorerManager is configured (by default it will turn the test image white) run `java RecolorerManager` 
