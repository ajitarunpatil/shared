# This android application shows the current weather information for the user's current location or searched location.
 
# Application follows MVVM architectural pattern using the following Android tech stack.
1.	Kotlin
2.	Dagger 2
3.	Coroutine
4.	Retrofit
5.	Room Database
6.	Jetpack compose
7.	Mockk mocking framework for Junit tests.
 
# Application considers following user scenarios.
1.	On app launch, the app checks for user location permissions. If location settings are enabled and location permission granted by the user, the app will display weather for the user's current location.
2.	If location permission is not enabled or granted, App checks the room database for the last searched location. 
a. If the last searched location is available, the app displays see weather information for the last searched location
b . If the last searched location is not available, the app displays an error state to the user and suggests searching by entering the location name.
     3. Users can get weather information by entering the location name. If the location name is valid, the app displays weather info for the searched location.
 
# Please note that this application currently uses API to display weather information by entering location name only. (It does not support search by only ZIP code)

