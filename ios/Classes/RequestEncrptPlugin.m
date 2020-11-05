#import "RequestEncrptPlugin.h"
#import "NSString+TPAPI.h"
#import "TPAccountHelper.h"

@implementation RequestEncrptPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"com.gugu.chuman/request_encrpt"
            binaryMessenger:[registrar messenger]];
  RequestEncrptPlugin* instance = [[RequestEncrptPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getSignature" isEqualToString:call.method]) {
    result([(NSString *)call.arguments tpAPI_getSignature]);
  } else if ([@"getPassword" isEqualToString:call.method]) {
    result([TPAccountHelper tp_encryptString:(NSString *)call.arguments]);
  }else {
    result(FlutterMethodNotImplemented);
  }
}

@end
