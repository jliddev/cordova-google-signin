# cordova-google-signin
Google sign in method following their instructions

## Setup

### All

Modify your `config.xml` file's `<widget>` tag's id property to match the one you setup in for your config file `<widget id="com.myname.myapp" ...` 

### Android

Follow this [guide](https://developers.google.com/identity/sign-in/android/start) to get all the required credentials setup.

The server client id is optional and can be set at runtime rather than through an XML string.

[Get a Config File](https://developers.google.com/mobile/add?platform=android&cntapi=signin&cntapp=Default%20Demo%20App&cntpkg=com.google.samples.quickstart.signin&cnturl=https:%2F%2Fdevelopers.google.com%2Fidentity%2Fsign-in%2Fandroid%2Fstart%3Fconfigured%3Dtrue&cntlbl=Continue%20with%20Try%20Sign-In)

## Install

`cordova plugin add https://github.com/jliddev/cordova-google-signin.git`

### Android
Replace `src/android/google-services.json` with the one you generated from the portal

## Usage

setServerClientId

```
onDeviceReady: function() {
     this.receivedEvent('deviceready');
     GoogleSignin.setServerClientId('myServerClientId')
 }
 ```
 
login

```
GoogleSignin.login({}, function(account){
   console.log('success') 
}, function(err){
    console.error(err);
});
```
