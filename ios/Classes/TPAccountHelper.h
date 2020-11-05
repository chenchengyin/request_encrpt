//
//  TPAccountHelper.h
//  Cheezz
//
//  Created by lht on 2020/9/10.
//  Copyright © 2020 iOS. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface TPAccountHelper : NSObject

+ (NSString *)tp_encryptString:(NSString *)str;
+ (NSString *)tp_encryptString:(NSString *)str publicKey:(NSString *)publicKey;
+ (NSString *)tp_decryptString:(NSString *)str privateKey:(NSString *)privKey;

@end

NS_ASSUME_NONNULL_END
