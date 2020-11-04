import 'dart:async';

import 'package:flutter/services.dart';

class RequestEncrpt {
  static const MethodChannel _channel =
      const MethodChannel('com.gugu.chuman/request_encrpt');

  static Future<String> getSignature(String message) async {
    final String version = await _channel.invokeMethod('getSignature', message);
    return version;
  }
}
