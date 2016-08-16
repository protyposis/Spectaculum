<img src="./spectaculum-logo.png" width="340" height="240" alt="Spectaculum Logo"/>

Spectaculum
===========

Spectaculum for Android is a hardware accelerated view for visual content with touch navigation, shader effects processing and frame extraction.
It provides a unified view for various image sources like picture files, bitmaps, camera, and videos from Android's MediaPlayer,
MediaPlayer-Extended and ExoPlayer.
Shader effects follow a simple pattern, can be applied to any source, parameterized and adjusted any time.
New sources can be easily integrated.

A [demo](https://play.google.com/store/apps/details?id=net.protyposis.android.spectaculumdemo) is available on the Google Play Store.


Features
--------

 * GLES shader support
 * Picture zooming/panning support
 * Frame extraction
 * Lightweight (all components total to ~100kB)


Changelog
---------

* __v1.0__: initial release


Requirements
------------

 * Android API 16+ (Android 4.1 Jelly Bean)
 * optional: Adreno GPU
 * optional: OpenGL ES 3.0


Usage
-----

### API ###



### Gradle ###

To use this library in your own project, you can either (1) fetch the modules from the
JCenter central Maven repository, or checkout the Git repository and (2) install the modules to
your local Maven repository or (3) include the required gradle modules directly.

#### JCenter repository ####

The [JCenter](https://bintray.com/bintray/jcenter) Maven repository contains release builds of the
library, usage is similar to any other Maven dependency:

    repositories {
        ...
        jcenter()
    }

    dependencies {
        ...
        compile 'net.protyposis.android.spectaculum.spectaculum:1.0.0'
        compile 'net.protyposis.android.spectaculum.spectaculum-camera:1.0.0'
        compile 'net.protyposis.android.spectaculum.spectaculum-mediaplayerextended:1.0.0'
        compile 'net.protyposis.android.spectaculum.spectaculum-effect-flowabs:1.0.0'
        compile 'net.protyposis.android.spectaculum.spectaculum-effect-immersive:1.0.0'
        compile 'net.protyposis.android.spectaculum.spectaculum-effect-qrmarker:1.0.0'
    }

#### Local Maven repository ####

Run `gradlew publishMavenPublicationToMavenLocal` to compile and install the modules to your
local Maven repository and add one or more of the following dependencies:

    repositories {
        ...
        mavenLocal()
    }

    dependencies {
        ...
        compile 'net.protyposis.android.spectaculum.spectaculum:1.0.0-SNAPSHOT'
        // etc, see above
    }


Components
----------

### Output Sources ###


#### Camera ####


#### MediaPlayer-Extended ####


#### Spectaculum-Effect-FlowAbs ####

This module adds the [FlowAbs](https://code.google.com/p/flowabs/) shader effect to the GLES component
and demonstrates the possibility to construct and use very elaborate shaders. It also offers various
sub-effects that the flowabs-effect is composed of, including (flow-based) difference of Gaussians,
color quantization and a tangent flow map.

#### Spectaculum-Effect-QrMarker ####

This module is another example of an effect composed of multiple shaders. It is taken from
[QrMarker](https://github.com/thHube/QrMarker-ComputerVision) and provides a rather pointless and
extremely slow QR marker identification effect, and a nice Canny edge detection effect.

#### Spectaculum-Demo ####

This module is a demo app that incorporates all the main functionality of the modules
and serves as an example on how they can be used. It is available for download as
[Spectaculum Demo](https://play.google.com/store/apps/details?id=net.protyposis.android.android.spectaculumdemo) on the Google Play Store.


Known Issues
------------

* Effect-FlowAbs: The OrientationAlignedBilateralFilterShaderProgram / FlowAbsBilateralFilterEffect does
  not work correctly for some unknown reason and is deactivated in the FlowAbs effect, making it
  slightly less fancy

Device specific:

* Camera: preview aspect ratio is slightly off on the Nexus 7 2013 back camera (seems to be a system bug)
* Effect-FlowAbs: Not working on Tegra devices because shaders contain dynamic loops

Tested and confirmed working on:

* LG Nexus 4 (Android 4.4.4/5.0/5.0.1/5.1.1, Adreno 320)
* LG Nexus 5 (Android 4.4.4/5.0/5.0.1, Adreno 330)
* ASUS Nexus 7 2012 (Android 4.4.4, Tegra 3, no FlowAbs)
* ASUS Nexus 7 2013 (Android 4.4.4/5.0/5.0.2, Adreno 320)
* ASUS Transformer TF701T (Android 4.4.2, Tegra 4, no FlowAbs)
* Samsung Galaxy SII (Android 4.1.2, ARM Mali-400MP4)
* Samsung Galaxy Note 2 (Android 4.4.4 CM, ARM Mali-400MP4)
* Samsung Galaxy Note 4 (Android 5.0.1, Snapdragon version with Adreno 420)


License
-------

Copyright (C) 2014, 2015, 2016 Mario Guggenberger <mg@protyposis.net>.
This project is released under the terms of the GNU General Public License. See `LICENSE` for details.
