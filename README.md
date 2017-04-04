# cordova-google-signin
google sign in method following their instructions

## Setup

### Android

Follow this [guide](https://developers.google.com/identity/sign-in/android/start) to get all the required credentials setup. This plugin requires both the config file, and the server client id.

[Get a Config File](https://developers.google.com/mobile/add?platform=android&cntapi=signin&cntapp=Default%20Demo%20App&cntpkg=com.google.samples.quickstart.signin&cnturl=https:%2F%2Fdevelopers.google.com%2Fidentity%2Fsign-in%2Fandroid%2Fstart%3Fconfigured%3Dtrue&cntlbl=Continue%20with%20Try%20Sign-In)

## Install

`cordova plugin add https://github.com/jliddev/cordova-google-signin.git --variable SERVER_CLIENT_ID="yourserverclientid"`

### Android
replace `src/android/google-services.json` with the one you generated from the portal
