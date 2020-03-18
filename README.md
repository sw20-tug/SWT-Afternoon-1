# SWT: Afternoon 1

This is the repository for the team *SWT: Afternoon 1*

## Cheat

Cheating app allows you to exchange messages with a cheating partner without the need of a mobile signal or an internet connection using a Bluetooth connection.

## Importing the Project

The easiest way to get started is to use Android Studio and to import the project.

1. Open Android Studio and click Import Project
2. Navigate to the cloned repository
3. Select the file `build.gradle` 
4. Android Studio will open the project and set it up. Time for a nice coffee ;)

## Building

Using Android Studio, build the project by clicking at the hammer icon on the top right.

Alternatively, you can build the project via the command line:

1. Navigate to the project root
2. Enter ./gradlew build
(make sure that you have the JDK in your PATH variable)

## Run

Using Android Studio, click the play icon on the top right. You can either run it on an Android Virtual Device or install it to a plugged in phone. Be sure to enable the Android Developer Options on your phone first though.

## Testing

To Execute tests via Android Studio you can do the following:

* Open the gradle tasks window (on the right side) and navigate to Tasks->verification
  * Double Click the gradle task "test"
* Open the test classes you want to execute and either execute the whole test class or specific tests by clicking on the respective play icons (left to the code window).

Alternatively, tests can be executed via the command line:

1. Navigate to the project root
2. Enter ./gradlew test for unit tests
3. Enter ./gradlew connectedAndroidTest for instrumented unit tests