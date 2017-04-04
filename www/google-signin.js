cordova.addConstructor(function() {
    function GoogleSignin() {

    }

    GoogleSignin.prototype.setServerClientId = function( serverClientId, successCallback, errorCallback ){
        cordova.exec(successCallback, errorCallback, "GoogleSignin", "setServerClientId", [serverClientId]);
    }

    GoogleSignin.prototype.login = function(options, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "GoogleSignin", "glogin", [options]);
    };

    window.GoogleSignin = new GoogleSignin()
    return window.GoogleSignin
});
