//
//  NSString+TPAPI.m
//  TouchManga
//
//  Created by qitang on 2017/8/22.
//  Copyright © 2017年 Guangzhou Dreampix Innovation Network Techology Co., Ltd. All rights reserved.
//

#import "NSString+TPAPI.h"
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonHMAC.h>
//#import "TPAPINetworking.h"

@implementation NSString (TPAPI)

+ (NSString*)tpAPI_unescapeUnicodeString:(NSString*)string{
    // unescape quotes and backwards slash
    NSString* unescapedString = [string stringByReplacingOccurrencesOfString:@"\\\"" withString:@"\""];
    unescapedString = [unescapedString stringByReplacingOccurrencesOfString:@"\\\\" withString:@"\\"];
    
    // tokenize based on unicode escape char
    NSMutableString* tokenizedString = [NSMutableString string];
    NSScanner* scanner = [NSScanner scannerWithString:unescapedString];
    while ([scanner isAtEnd] == NO)
    {
        // read up to the first unicode marker
        // if a string has been scanned, it's a token
        // and should be appended to the tokenized string
        NSString* token = @"";
        [scanner scanUpToString:@"\\u" intoString:&token];
        if (token != nil && token.length > 0)
        {
            [tokenizedString appendString:token];
            continue;
        }
        
        // skip two characters to get past the marker
        // check if the range of unicode characters is
        // beyond the end of the string (could be malformed)
        // and if it is, move the scanner to the end
        // and skip this token
        NSUInteger location = [scanner scanLocation];
        NSInteger extra = scanner.string.length - location - 4 - 2;
        if (extra < 0)
        {
            NSRange range = {location, -extra};
            [tokenizedString appendString:[scanner.string substringWithRange:range]];
            [scanner setScanLocation:location - extra];
            continue;
        }
        
        // move the location pas the unicode marker
        // then read in the next 4 characters
        location += 2;
        NSRange range = {location, 4};
        token = [scanner.string substringWithRange:range];
        unichar codeValue = (unichar) strtol([token UTF8String], NULL, 16);
        [tokenizedString appendString:[NSString stringWithFormat:@"%C", codeValue]];
        
        // move the scanner past the 4 characters
        // then keep scanning
        location += 4;
        [scanner setScanLocation:location];
    }
    
    // done
    return tokenizedString;
}

+ (NSString*)tpAPI_escapeUnicodeString:(NSString*)string{
    // lastly escaped quotes and back slash
    // note that the backslash has to be escaped before the quote
    // otherwise it will end up with an extra backslash
    NSString* escapedString = [string stringByReplacingOccurrencesOfString:@"\\" withString:@"\\\\"];
    escapedString = [escapedString stringByReplacingOccurrencesOfString:@"\"" withString:@"\\\""];
    
    // convert to encoded unicode
    // do this by getting the data for the string
    // in UTF16 little endian (for network byte order)
    NSData* data = [escapedString dataUsingEncoding:NSUTF16LittleEndianStringEncoding allowLossyConversion:YES];
    size_t bytesRead = 0;
    const char* bytes = data.bytes;
    NSMutableString* encodedString = [NSMutableString string];
    
    // loop through the byte array
    // read two bytes at a time, if the bytes
    // are above a certain value they are unicode
    // otherwise the bytes are ASCII characters
    // the %C format will write the character value of bytes
    while (bytesRead < data.length)
    {
        NSString *charS = nil;
        uint16_t code = *((uint16_t*) &bytes[bytesRead]);
        if (code > 0x007E)
        {
            charS = [NSString stringWithFormat:@"\\u%04X", code];
            [encodedString appendString:[charS lowercaseString]];
        }
        else
        {
            charS = [NSString stringWithFormat:@"%C", code];
            [encodedString appendString:charS];
        }
        bytesRead += sizeof(uint16_t);
    }
    
    // makes u an uppercase
    [encodedString stringByReplacingOccurrencesOfString:@"\\U" withString:@"\\u"];
    
    // done
    return encodedString;
}

- (NSString *)tpAPI_MD5{
    // Create pointer to the string as UTF8
    const char *ptr = [self UTF8String];
    
    // Create byte array of unsigned chars
    unsigned char md5Buffer[CC_MD5_DIGEST_LENGTH];
    
    // Create 16 byte MD5 hash value, store in buffer
    CC_MD5(ptr, (CC_LONG)strlen(ptr), md5Buffer);
    
    // Convert MD5 value in the buffer to NSString of hex values
    NSMutableString *output = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH * 2];
    for(int i = 0; i < CC_MD5_DIGEST_LENGTH; i++)
        [output appendFormat:@"%02x",md5Buffer[i]];
    
    return output;
}

- (NSString *)tpAPI_getSignature{
    NSString *str = [@"0b379ed3f4f361a68859dece9ee632fe" stringByAppendingString:self];
    
    NSString *appSecrect = @"d2a88d717683c58b66965c8187adb4f5";
    str = [self tpAPI_encrypt:str
                          key:appSecrect];
    
    NSData *hmacData = [self tpAPI_getHMACSHA1:str
                                           key:appSecrect];
    
    str = [hmacData base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
    
    return str;
}

- (NSString *)tpAPI_encrypt:(NSString *)data key:(NSString *)pKey{
    NSString *expire = @"0000000000";
    
    // string in objective c are all utf8
    NSString *key = [pKey tpAPI_MD5];
    NSString *parsed = [expire stringByAppendingString:data];
    NSData *plainData = [parsed dataUsingEncoding:NSASCIIStringEncoding];
    
    data = [plainData base64EncodedStringWithOptions:0];
    
    NSUInteger x = 0;
    NSUInteger len = data.length;
    NSUInteger l = key.length;
    
    NSMutableString *c = [[NSMutableString alloc] init];
    
    for(NSUInteger i = 0; i < len; i++) {
        if(x == l) x = 0;
        [c appendFormat:@"%c", [key characterAtIndex:x]];
        x++;
    }
    
    NSMutableData *resultData = [NSMutableData data];
    for(NSUInteger i = 0; i < len; i++) {
        int char1 = [data characterAtIndex:i];
        int char2 = [c characterAtIndex:i] % 256;
        
        int result = char1 + char2;
        [resultData appendBytes:&result length:1];
    }
    
    // this is as is encoding because encryption is done on char level
    NSString *result = [resultData base64EncodedStringWithOptions:0];
    
    return result;
}

- (NSData *)tpAPI_getHMACSHA1:(NSString *)data key:(NSString *)key{
    const char *cKey  = [key cStringUsingEncoding:NSASCIIStringEncoding];
    const char *cData = [data cStringUsingEncoding:NSASCIIStringEncoding];
    
    unsigned char cHMAC[CC_SHA1_DIGEST_LENGTH];
    
    CCHmac(kCCHmacAlgSHA1, cKey, strlen(cKey), cData, strlen(cData), cHMAC);
    
    NSData *HMAC = [[NSData alloc] initWithBytes:cHMAC length:sizeof(cHMAC)];
    
    return HMAC;
}

+ (NSString *)tpAPI_getContentLocale{
    NSString *contentLocale = nil;
    NSString *currentLanguage = [[NSLocale preferredLanguages] objectAtIndex:0];
    NSString *countryCode = [[NSLocale currentLocale] objectForKey:NSLocaleCountryCode];
    
    if([currentLanguage isEqualToString:@"zh-Hans"]
       || [countryCode isEqualToString:@"CN"]){
        contentLocale = @"zh-cn";
    }else{
        contentLocale = @"en-us";
    }
    return contentLocale;
}

@end
