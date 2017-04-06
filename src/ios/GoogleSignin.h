#import <Cordova/CDVPlugin.h>
#import <GoogleSignIn/GoogleSignIn.h>

@interface GoogleSignin : CDVPlugin<GIDSignInDelegate, GIDSignInUIDelegate>

@property (nonatomic, copy) NSString* loginCallbackId;
@property (nonatomic, assign) BOOL isSigningIn;

- (void) glogin:(CDVInvokedUrlCommand*)command;

@end