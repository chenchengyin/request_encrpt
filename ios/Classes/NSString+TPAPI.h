//
//  NSString+TPAPI.h
//  TouchManga
//
//  Created by qitang on 2017/8/22.
//  Copyright © 2017年 Guangzhou Dreampix Innovation Network Techology Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString (TPAPI)

+ (NSString*)tpAPI_unescapeUnicodeString:(NSString*)string;

+ (NSString*)tpAPI_escapeUnicodeString:(NSString*)string;

- (NSString *)tpAPI_getSignature;

- (NSString *)tpAPI_encrypt:(NSString *)data key:(NSString *)pKey;

- (NSData *)tpAPI_getHMACSHA1:(NSString *)data key:(NSString *)key;

+ (NSString *)tpAPI_getContentLocale;

@end
