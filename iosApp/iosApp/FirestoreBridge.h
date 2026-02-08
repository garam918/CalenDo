#import <Foundation/Foundation.h>

@interface FirestoreBridge : NSObject

+ (void)addDocument:(NSString *)collection data:(NSDictionary *)data;
+ (void)getDocuments:(NSString *)collection completion:(void (^)(NSArray *docs))completion;

@end
