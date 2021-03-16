# Android_hr_emotion_recognizer

This is a android library that connects to Bluetooth HR sensor, subscribes to it and shows current HR readings.\
Additionaly, based on HR data and specified age it determines user's emotion. Available states are: bored, neutral, stressed.\

Library contains HREmotionRecognizer class that takes HRChangeListener to wchich hr radings are propagated. It also contains ready DialogFragment that displays list of available HR devices andlets user choose which one to connect to.

This project contains simple app that showcases library functionality.  

Currently supported HR Bluetooth device is Polar h10. Library is using official Polar sdk for communication with sensor.
