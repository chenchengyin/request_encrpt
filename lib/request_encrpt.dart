import 'dart:async';

import 'package:flutter/services.dart';

class RequestEncrpt {
  static const MethodChannel _channel =
      const MethodChannel('com.gugu.chuman/request_encrpt');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
