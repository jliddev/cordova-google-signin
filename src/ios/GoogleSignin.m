#import "AppDelegate.h"
#import "objc/runtime.h"
#import "GoogleSignin.h"

static void swizzleMethod(Class class, SEL destinationSelector, SEL sourceSelector);

@implementation AppDelegate (IdentityUrlHandling)

+ (void)load {
    
    swizzleMethod([AppDelegate class],
                  @selector(application:openURL:options:),
                  @selector(indentity_application_options:openURL:options:));
}

- (BOOL)indentity_application_options: (UIApplication *)app
                              openURL: (NSURL *)url
                              options: (NSDictionary *)options
{
    GoogleSignin* gs = (GoogleSignin*) [self.viewController pluginObjects][@"GoogleSignin"];
    
    if ([gs isSigningIn]) {
        gs.isSigningIn = NO;
        return [[GIDSignIn sharedInstance] handleURL:url
                                   sourceApplication:options[UIApplicationOpenURLOptionsSourceApplicationKey]
                                          annotation:options[UIApplicationOpenURLOptionsAnnotationKey]];
    } else {
        // Other
        return [self application:app openURL:url
               sourceApplication:options[UIApplicationOpenURLOptionsSourceApplicationKey]
                      annotation:options[UIApplicationOpenURLOptionsAnnotationKey]];
    }
}

@end

@implementation GoogleSignin

- (void)pluginInitialize
{
    [super pluginInitialize];
    
    self.isSigningIn = NO;
    
    [self initializeGoogle];
}

-(void) initializeGoogle
{
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"GoogleService-Info" ofType:@"plist"];
    if( filePath == nil ){
        NSLog(@"Failed to find GooglerService-Info.plist");
        return;
    }
    NSDictionary* plist = [NSDictionary dictionaryWithContentsOfFile:filePath];
    NSString* clientId = plist[@"CLIENT_ID"];
    if( clientId == nil ){
        NSLog(@"Failed to find client ID in plist");
        return;
    }
    
    [GIDSignIn sharedInstance].clientID = clientId;
    [GIDSignIn sharedInstance].delegate = self;
    [GIDSignIn sharedInstance].uiDelegate = self;
}

- (void) glogin:(CDVInvokedUrlCommand*)command
{
    self.isSigningIn = YES;
    self.loginCallbackId = command.callbackId;
    
    [[GIDSignIn sharedInstance] signIn];
    
//    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:YES];
//    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

#pragma mark GIDSignInUIDelegate

// pressed the Sign In button
- (void)signInWillDispatch:(GIDSignIn *)signIn error:(NSError *)error {
//    [myActivityIndicator stopAnimating];
}

// Present a view that prompts the user to sign in with Google
- (void)signIn:(GIDSignIn *)signIn
presentViewController:(UIViewController *)viewController {
    [self.viewController presentViewController:viewController animated:YES completion:nil];
}

// Dismiss the "Sign in with Google" view
- (void)signIn:(GIDSignIn *)signIn
dismissViewController:(UIViewController *)viewController {
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark GIDSignInDelegate

- (void)signIn:(GIDSignIn *)signIn
didSignInForUser:(GIDGoogleUser *)user
     withError:(NSError *)error
{
    NSLog(@"");
    
    NSMutableDictionary* response = [[NSMutableDictionary alloc] init];
    response[@"displayName"] = user.profile.name;
    response[@"email"] = user.profile.email;
    response[@"familyName"] = user.profile.familyName;
    response[@"givenName"] = user.profile.givenName;
    response[@"id"] = user.userID;
    response[@"idToken"] = user.authentication.idToken;
    response[@"photoUrl"] = [[user.profile imageURLWithDimension:100] absoluteString];
    
    CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:response];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.loginCallbackId];
}

- (void)signIn:(GIDSignIn *)signIn
didDisconnectWithUser:(GIDGoogleUser *)user
     withError:(NSError *)error
{
    NSLog(@"");
}

@end

#pragma mark Swizzling

static void swizzleMethod(Class class, SEL destinationSelector, SEL sourceSelector) {
    Method destinationMethod = class_getInstanceMethod(class, destinationSelector);
    Method sourceMethod = class_getInstanceMethod(class, sourceSelector);
    
    // If the method doesn't exist, add it.  If it does exist, replace it with the given implementation.
    if (class_addMethod(class, destinationSelector, method_getImplementation(sourceMethod), method_getTypeEncoding(sourceMethod))) {
        class_replaceMethod(class, destinationSelector, method_getImplementation(destinationMethod), method_getTypeEncoding(destinationMethod));
    } else {
        method_exchangeImplementations(destinationMethod, sourceMethod);
    }
}
