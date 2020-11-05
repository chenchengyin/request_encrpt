//
//  TPAccountHelper.m
//  Cheezz
//
//  Created by lht on 2020/9/10.
//  Copyright © 2020 iOS. All rights reserved.
//

#import "TPAccountHelper.h"
#import "RSA.h"

NSString *const kRSAPublicKey = @"-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCtGIiuszQ8eubb9tWX7N00GD0s\nV8930ZMB7aXwxH4vZlomIEjVdz3J5EDkhMvLLkcboFePae3GsIZlD2q4HF1yCrJe\n3emoh360/MaZ9/AznP3creL4NCw0v78kRkoeJQ4oifkNy0GhUOA6U23mlgm/h+cq\neC8J5NuuX/somCLMMwIDAQAB\n-----END PUBLIC KEY-----";

@implementation TPAccountHelper

+ (NSString *)tp_encryptString:(NSString *)str {
    return [self tp_encryptString:str publicKey:kRSAPublicKey];;
}

+ (NSString *)tp_encryptString:(NSString *)str publicKey:(NSString *)publicKey {
    if (str.length==0) {
        return nil;
    }
    NSString *result = [RSA encryptString:str publicKey:kRSAPublicKey];
//    TPLog(@"encrypt result %@",result);
    result = [result stringByReplacingOccurrencesOfString:@"+" withString:@"-"];
    result = [result stringByReplacingOccurrencesOfString:@"/" withString:@"_"];
    result = [result stringByReplacingOccurrencesOfString:@"=" withString:@""];
//    TPLog(@"encrypt result after replace %@",result);
    return result;
}

+ (NSString *)tp_decryptString:(NSString *)str privateKey:(NSString *)privKey {
    if (str.length==0 || privKey.length==0) {
        return nil;
    }
    NSString *decryptStr = str;
    decryptStr = [decryptStr stringByReplacingOccurrencesOfString:@"-" withString:@"+"];
    decryptStr = [decryptStr stringByReplacingOccurrencesOfString:@"_" withString:@"/"];
    decryptStr = [decryptStr stringByReplacingOccurrencesOfString:@" " withString:@"="];
    NSString *result = [RSA decryptString:decryptStr privateKey:privKey];
    return result;
}


@end
