//
//  PublicMethod.h
//  PinkPavsion
//
//  Created by infiart studio on 12-6-7.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <QuartzCore/QuartzCore.h>

@interface Utils : NSObject
{
    
}


//获取屏幕宽度
+(float) getWindowWidth;

//获取屏幕高度
+(float) getWindowHigh;
//unicode转中文
+ (NSString *)replaceUnicode:(NSString *)unicodeStr;

//中文转unicode
+ (NSString *)replaceChinese:(NSString *)ChineseStr;

//缩放图片到指定size
+(UIImage*)  OriginImage:(UIImage *)image   scaleToSize:(CGSize)size;

//画虚线
+(UIImageView *) createLineType1:(int)x  lastY:(int)y  width:(int)w  Hight:(int) h;

+(UIImageView *) createLineType2:(int)x  lastY:(int)y  width:(int)w  Hight:(int) h;

//UIColor to UIImage
+(UIImage *)createImageWithColor: (UIColor*) color;

//获取屏幕尺寸 kind 0:app尺寸(不带状态栏) 1:屏幕尺寸
+(CGRect) getScreenSize:(NSInteger) kind;

/*获取6位随机数*/
+(NSString *)RandomNum;

//md5
+(NSString *)md5:(NSString *)input;

//截屏
+ (void)ScreenShots:(UIView *)view title:(NSString *)strTitle;

////iphone生成随机uuid串的代码
//+ (NSString *)stringWithUUID;

//字符串转换时间格式
+ (NSDate *)dateFromString:(NSString *)string;

//时间转换字符串
+ (NSString *)fixStringForDate:(NSDate *)date;

+(NSString*)fixCreateDate:(NSDate *)date;

+(NSDate *)dateFromStr:(NSString *)dateString;

//解析Base64
+ (NSString*)decodeBase64:(NSString*)input;


//获取localIP
+(NSString *)getLocalIpAddress;

//获取mac地址
+ (NSString *)getMacAddress;

//根据字符 字体计算控件宽高
+(CGSize)AutoSzie:(NSString *)myText myFont:(UIFont *)myFont;

//获取机器型号
+ (NSString *) platformString;

//是否隐藏tabbar
+ (void) hideTabBar:(UITabBarController *) tabbarcontroller;
+ (void) showTabBar:(UITabBarController *) tabbarcontroller;

//清除UITableView底部多余的分割线。
+ (void)setExtraCellLineHidden: (UITableView *)tableView;

//获取时间戳
+ (NSString *)UNIX_TIMESTAMP;

//汉字转Unicode编码
+(NSString *) utf8ToUnicode:(NSString *)string;

//验证手机号码
+ (BOOL)validateMobile:(NSString *)mobileNum;

//验证身份证号码合法性
+ (BOOL)verifyIDCardNumber:(NSString *)value;

//从userdefault中获取特定值
+ (NSString *)getValueFromUserDefaultWithKey:(NSString *)key;
@end
