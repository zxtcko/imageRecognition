//
//  PublicMethod.m
//  PinkPavsion
//
//  Created by infiart studio on 12-6-7.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "Utils.h"

#include <sys/types.h>
#include <sys/socket.h>  
#include <sys/sysctl.h>  
#include <net/if.h>  
#include <net/if_dl.h> 
#import <netinet/in.h>
#import <arpa/inet.h>
#import <netdb.h>
#import <SystemConfiguration/SCNetworkReachability.h>
#import <CommonCrypto/CommonDigest.h>
#include <ifaddrs.h>
#include <arpa/inet.h>




@implementation Utils

/**
 *  初始化创建对象
 *
 *  @return Utils对象
 */
+ (instancetype)util
{
    Utils *util = [[Utils alloc] init];
    return util;
}

+(NSString *)replaceUnicode:(NSString *)unicodeStr {
    NSString *tempStr1 = [unicodeStr stringByReplacingOccurrencesOfString:@"\\u" withString:@"\\U"];
    NSString *tempStr2 = [tempStr1 stringByReplacingOccurrencesOfString:@"\"" withString:@"\\\""];
    NSString *tempStr3 = [[@"\"" stringByAppendingString:tempStr2] stringByAppendingString:@"\""];
    NSData *tempData = [tempStr3 dataUsingEncoding:NSUTF8StringEncoding];
    NSString* returnStr = [NSPropertyListSerialization propertyListFromData:tempData
                                                           mutabilityOption:NSPropertyListImmutable
                                                                     format:NULL
                                                           errorDescription:NULL];
    
    return [returnStr stringByReplacingOccurrencesOfString:@"\\r\\n" withString:@"\n"];
}

+(float) getWindowWidth{
    return [[UIScreen mainScreen] bounds].size.width;
}

+(float) getWindowHigh{
    return [[UIScreen mainScreen] bounds].size.height;
}
+(UIImage*)  OriginImage:(UIImage *)image   scaleToSize:(CGSize)size
{
    if([[UIScreen mainScreen] scale] == 2.0){
        UIGraphicsBeginImageContextWithOptions(size, NO, 2.0);
    }else{
        UIGraphicsBeginImageContext(size);
    }
    // 绘制改变大小的图片
    [image drawInRect:CGRectMake(0, 0, size.width, size.height)];
    // 从当前context中创建一个改变大小后的图片
    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    // 使当前的context出堆栈
    UIGraphicsEndImageContext();
    // 返回新的改变大小后的图片
    return scaledImage;
}

//画虚线
+(UIImageView *) createLineType1:(int)x  lastY:(int)y  width:(int)w  Hight:(int) h{
    UIImageView *imageView1 = [[UIImageView alloc]initWithFrame:CGRectMake(x, y, w, h)];
    
    UIGraphicsBeginImageContext(imageView1.frame.size);   //开始画线
    [imageView1.image drawInRect:CGRectMake(0, 0, imageView1.frame.size.width, imageView1.frame.size.height)];
    CGContextSetLineCap(UIGraphicsGetCurrentContext(), kCGLineCapRound);  //设置线条终点形状
    
    CGFloat lengths[] = {10,5};
    CGContextRef line = UIGraphicsGetCurrentContext();
    CGContextSetStrokeColorWithColor(line, [UIColor colorWithRed:189/255.0  green:189/255.0 blue:189/255.0 alpha:1].CGColor);
    
    CGContextSetLineDash(line, x, lengths, 2);  //画虚线
    CGContextMoveToPoint(line, x, h);    //开始画线
    CGContextAddLineToPoint(line, w, h);
    CGContextStrokePath(line);
    
    imageView1.image = UIGraphicsGetImageFromCurrentImageContext();

    return imageView1;
}

+(UIImageView *) createLineType2:(int)x  lastY:(int)y  width:(int)w  Hight:(int) h{
    UIImageView *imageView1 = [[UIImageView alloc]initWithFrame:CGRectMake(x, y, w, h)];
    
    UIGraphicsBeginImageContext(imageView1.frame.size);   //开始画线
    [imageView1.image drawInRect:CGRectMake(0, 0, imageView1.frame.size.width, imageView1.frame.size.height)];
    CGContextSetLineCap(UIGraphicsGetCurrentContext(), kCGLineCapRound);  //设置线条终点形状
    
    CGFloat lengths[] = {5,2};
    CGContextRef line = UIGraphicsGetCurrentContext();
    CGContextSetStrokeColorWithColor(line, [UIColor colorWithRed:189/255.0  green:189/255.0 blue:189/255.0 alpha:1].CGColor);
    
    CGContextSetLineDash(line, x, lengths, 2);  //画虚线
    CGContextMoveToPoint(line, x, h);    //开始画线
    CGContextAddLineToPoint(line, w, h);
    CGContextStrokePath(line);
    
    imageView1.image = UIGraphicsGetImageFromCurrentImageContext();
    
    return imageView1;
}


//UIColor to UIImage
+(UIImage*) createImageWithColor:(UIColor*)color{
    CGRect rect=CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    UIImage *theImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return theImage;
}

//获取屏幕尺寸
+(CGRect) getScreenSize:(NSInteger) kind
{
    CGRect r;
    
    if (kind==0)
    {
        r = [ UIScreen mainScreen ].applicationFrame;
    }
    else if (kind==1)
    {
        r = [ UIScreen mainScreen ].bounds;
    }
    return r;
}

/*获取6位随机数*/
+(NSString *)RandomNum
{
    int rdmNum =  (arc4random() % 899999) + 100000;
    NSString * numStr = [NSString stringWithFormat:@"%d",rdmNum];
    return numStr;
}



//md5加密 小写
+(NSString *)md5:(NSString *)input
{
    /* 大写
    const char *cStr = [input UTF8String];
    unsigned char result[16];
    CC_MD5( cStr, strlen(cStr), result );
    return [NSString stringWithFormat:
            @"%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X%02X",
            result[0], result[1], result[2], result[3], 
            result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11],
            result[12], result[13], result[14], result[15]
            ]; 
    */
    const char *cStr = [input UTF8String];
   
    unsigned char result[32];
    
    //CC_MD5( cStr, strlen(cStr), result );
    CC_MD5( cStr, strlen(cStr), result );
    
    return [NSString stringWithFormat: 
            
            @"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
            
            result[0], result[1], result[2], result[3], 
            
            result[4], result[5], result[6], result[7], 
            
            result[8], result[9], result[10], result[11], 
            
            result[12], result[13], result[14], result[15] 
            
            ]; 
}

//截屏
+ (void)ScreenShots:(UIView *)view title:(NSString *)strTitle
{
    //支持retina高分的关键
    if(&UIGraphicsBeginImageContextWithOptions != NULL)
    {
        UIGraphicsBeginImageContextWithOptions(view.frame.size, NO, 0.0);
    } else {
        UIGraphicsBeginImageContext(view.frame.size);
    }
    
    //    UIGraphicsBeginImageContext(view.bounds.size);
	[view.layer renderInContext:UIGraphicsGetCurrentContext()];
	
	UIImage *image= UIGraphicsGetImageFromCurrentImageContext();
	UIGraphicsEndImageContext();
	
    //保存图像
    NSString *path = [NSHomeDirectory() stringByAppendingFormat:@"/%@.png",strTitle];
    [UIImagePNGRepresentation(image) writeToFile:path atomically:YES];
	UIImageWriteToSavedPhotosAlbum(image, self, nil, nil);   
}



//字符串转化时间格式
+ (NSDate *)dateFromString:(NSString *)string {
    //Wed Mar 14 16:40:08 +0800 2012
    if (!string) return nil;
    
    struct tm tm;
    time_t t;
    string=[string substringFromIndex:4];
    strptime([string cStringUsingEncoding:NSUTF8StringEncoding], "%a, %d %b %Y %H:%M:%S %z", &tm);
    tm.tm_isdst = -1;
    t = mktime(&tm);
    return [NSDate dateWithTimeIntervalSince1970:t];
}

//时间转换字符串
+ (NSString *)fixStringForDate:(NSDate *)date
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterFullStyle];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *fixString = [dateFormatter stringFromDate:date];

    
    return fixString;
}

+(NSString*)fixCreateDate:(NSDate *)date
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterFullStyle];
    [dateFormatter setDateFormat:@"yyMMddHHmmss"];
    NSString *fixString = [dateFormatter stringFromDate:date];

    
    return fixString;
}

+(NSDate *)dateFromStr:(NSString *)dateString{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat: @"yyyy-MM-dd HH:mm:ss"]; 
    
    NSDate *destDate= [dateFormatter dateFromString:dateString];
    

    return destDate;
}

//解析Base64
+ (NSString*)decodeBase64:(NSString*)input {
    NSString* alphabet = @"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    NSString* decoded = @"";
    
    NSString* encoded = [input stringByPaddingToLength:(ceil([input length] / 4)*4)
                                            withString:@"A"
                                       startingAtIndex:0];
    
    int i;
    char a, b, c, d;
    UInt32 z;
    
    for(i = 0; i < [encoded length]; i += 4) {
        a = [alphabet rangeOfString:[encoded substringWithRange:NSMakeRange(i + 0, 1)]].location;
        b = [alphabet rangeOfString:[encoded substringWithRange:NSMakeRange(i + 1, 1)]].location;
        c = [alphabet rangeOfString:[encoded substringWithRange:NSMakeRange(i + 2, 1)]].location;
        d = [alphabet rangeOfString:[encoded substringWithRange:NSMakeRange(i + 3, 1)]].location;
        
        z = ((UInt32)a << 26) + ((UInt32)b << 20) + ((UInt32)c << 14) + ((UInt32)d << 8);
        decoded = [decoded stringByAppendingString:[NSString stringWithCString:(char *)&z encoding:NSUTF8StringEncoding]];
    }
    
    return decoded;
}


//获取localIP
+(NSString *)getLocalIpAddress 
{
    NSString *address = @"error";
	struct ifaddrs *interfaces = NULL;
	struct ifaddrs *temp_addr = NULL;
	int success = 0;
    
	// retrieve the current interfaces - returns 0 on success
	success = getifaddrs(&interfaces);
	if (success == 0)
	{
		// Loop through linked list of interfaces
		temp_addr = interfaces;
		while(temp_addr != NULL)
		{
			if(temp_addr->ifa_addr->sa_family == AF_INET)
			{
				// Check if interface is en0 which is the wifi connection on the iPhone
				if([[NSString stringWithUTF8String:temp_addr->ifa_name] isEqualToString:@"en1"])
				{
					// Get NSString from C String
					address = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr)];
				}
			}
            
			temp_addr = temp_addr->ifa_next;
		}
	}
    
	// Free memory
	freeifaddrs(interfaces);
    
	return address;
}


//获取mac地址
+ (NSString *)getMacAddress  
{  
    int                 mgmtInfoBase[6];  
    char                *msgBuffer = NULL;  
    size_t              length;  
    unsigned char       macAddress[6];  
    struct if_msghdr    *interfaceMsgStruct;  
    struct sockaddr_dl  *socketStruct;  
    NSString            *errorFlag = NULL;  
    
    // Setup the management Information Base (mib)  
    mgmtInfoBase[0] = CTL_NET;        // Request network subsystem  
    mgmtInfoBase[1] = AF_ROUTE;       // Routing table info  
    mgmtInfoBase[2] = 0;                
    mgmtInfoBase[3] = AF_LINK;        // Request link layer information  
    mgmtInfoBase[4] = NET_RT_IFLIST;  // Request all configured interfaces  
    
    // With all configured interfaces requested, get handle index  
    if ((mgmtInfoBase[5] = if_nametoindex("en0")) == 0)   
        errorFlag = @"if_nametoindex failure";  
    else  
    {  
        // Get the size of the data available (store in len)  
        if (sysctl(mgmtInfoBase, 6, NULL, &length, NULL, 0) < 0)   
            errorFlag = @"sysctl mgmtInfoBase failure";  
        else  
        {  
            // Alloc memory based on above call  
            if ((msgBuffer = malloc(length)) == NULL)  
                errorFlag = @"buffer allocation failure";  
            else  
            {  
                // Get system information, store in buffer  
                if (sysctl(mgmtInfoBase, 6, msgBuffer, &length, NULL, 0) < 0)  
                    errorFlag = @"sysctl msgBuffer failure";  
            }  
        }  
    }  
    
    // Befor going any further...  
    if (errorFlag != NULL)  
    {  
        NSLog(@"Error: %@", errorFlag);  
        return errorFlag;  
    }  
    
    // Map msgbuffer to interface message structure  
    interfaceMsgStruct = (struct if_msghdr *) msgBuffer;  
    
    // Map to link-level socket structure  
    socketStruct = (struct sockaddr_dl *) (interfaceMsgStruct + 1);  
    
    // Copy link layer address data in socket structure to an array  
    memcpy(&macAddress, socketStruct->sdl_data + socketStruct->sdl_nlen, 6);  
    
    // Read from char array into a string object, into traditional Mac address format  
    NSString *macAddressString = [NSString stringWithFormat:@"%02X:%02X:%02X:%02X:%02X:%02X",   
                                  macAddress[0], macAddress[1], macAddress[2],   
                                  macAddress[3], macAddress[4], macAddress[5]];  
    NSLog(@"Mac Address: %@", macAddressString);  
    
    // Release the buffer memory  
    free(msgBuffer);  
    return macAddressString;  
}

//根据字符 字体计算控件宽高
+ (CGSize)AutoSzie:(NSString *)myText myFont:(UIFont *)myFont 
{
    //获取到文本大大小
    CGFloat constrainedSize = 265.0f; //其他大小也行
    CGSize textSize = [myText sizeWithFont: myFont
                         constrainedToSize:CGSizeMake(constrainedSize, CGFLOAT_MAX)
                              lineBreakMode:NSLineBreakByWordWrapping];
    
    return textSize;
}

//获取机器型号
+ (NSString *) platformString
{ 
    size_t size;  
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);  
    char *machine = malloc(size);  
    sysctlbyname("hw.machine", machine, &size, NULL, 0);  
    NSString *platform = [NSString stringWithCString:machine encoding:NSUTF8StringEncoding];  
    free(machine); 
    
    if ([platform isEqualToString:@"iPhone1,1"])    return @"iPhone 1G";  
    if ([platform isEqualToString:@"iPhone1,2"])    return @"iphone3";//@"iPhone 3G";  
    if ([platform isEqualToString:@"iPhone2,1"])    return @"iPhone3s";//@"iPhone 3GS";  
    if ([platform isEqualToString:@"iPhone3,1"])    return @"iPhone 4";  
    if ([platform isEqualToString:@"iPhone4,1"])    return @"iPhone 4S";
    if ([platform isEqualToString:@"iPod1,1"])      return @"iPod Touch 1G";  
    if ([platform isEqualToString:@"iPod2,1"])      return @"iPod Touch 2G";  
    if ([platform isEqualToString:@"iPod3,1"])      return @"iPod Touch 3G";  
    if ([platform isEqualToString:@"iPod4,1"])      return @"iPod Touch 4G";  
    if ([platform isEqualToString:@"iPad1,1"])      return @"iPad";  
    if ([platform isEqualToString:@"iPad2,1"])      return @"iPad2";  
    if ([platform isEqualToString:@"i386"] || [platform isEqualToString:@"x86_64"])        
        return @"iPhone4";//@"iPhone Simulator";  
    
    return platform;  
} 

//是否隐藏tabbar
+ (void) hideTabBar:(UITabBarController *) tabbarcontroller {
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0];
    for(UIView *view in tabbarcontroller.view.subviews)
    {
        if([view isKindOfClass:[UITabBar class]])
        {
            [view setFrame:CGRectMake(view.frame.origin.x, 480, view.frame.size.width, view.frame.size.height)];
        } 
        else
        {
            [view setFrame:CGRectMake(view.frame.origin.x, view.frame.origin.y, view.frame.size.width, 480)];
        }
    }
    [UIView commitAnimations];
}

+ (void) showTabBar:(UITabBarController *) tabbarcontroller {
    
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0];
    for(UIView *view in tabbarcontroller.view.subviews)
    {
        NSLog(@"%@", view);
        
        if([view isKindOfClass:[UITabBar class]])
        {
            [view setFrame:CGRectMake(view.frame.origin.x, 431, view.frame.size.width, view.frame.size.height)];
        } 
        else
        {
            [view setFrame:CGRectMake(view.frame.origin.x, view.frame.origin.y, view.frame.size.width, 431)];
        }
    }
    
    [UIView commitAnimations]; 
}

//清除UITableView底部多余的分割线。
+ (void)setExtraCellLineHidden: (UITableView *)tableView
{
    UIView *view = [UIView new];
    view.backgroundColor = [UIColor clearColor];
    [tableView setTableFooterView:view];
 
}

//获取时间戳
+ (NSString *)UNIX_TIMESTAMP
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];   
    [formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss"]; // ----------设置你想要的格式,hh与HH的区别:分别表示12小时制,24小时制
    NSDate *datenow = [NSDate date];//现在时间,你可以输出来看下是什么格式
    //时间转时间戳的方法:
    NSString *timeSp = [NSString stringWithFormat:@"%ld", (long)[datenow timeIntervalSince1970]];
    return timeSp;
}


+ (NSString *)replaceChinese:(NSString *)ChineseStr
{
    char *p = (char *) [ChineseStr cStringUsingEncoding:NSUnicodeStringEncoding];
    NSString *str2 = [NSString stringWithFormat:@"%s",p];
    NSLog(@"%s, %@",p,str2);
//    NSString *str2 = [ChineseStr stringByAddingPercentEscapesUsingEncoding:NSUnicodeStringEncoding];
    return str2;
}

+(NSString *) utf8ToUnicode:(NSString *)string

{
    
    NSUInteger length = [string length];
    
    NSMutableString *s = [NSMutableString stringWithCapacity:0];
    
    for (int i = 0;i < length; i++)
        
    {
        
        unichar _char = [string characterAtIndex:i];
        
        //判断是否为英文和数字
        
        if (_char <= '9' && _char >='0')
            
        {
            
            [s appendFormat:@"%@",[string substringWithRange:NSMakeRange(i,1)]];
            
        }
        
        else if(_char >='a' && _char <= 'z')
            
        {
            
            [s appendFormat:@"%@",[string substringWithRange:NSMakeRange(i,1)]];
            
            
            
        }
        
        else if(_char >='A' && _char <= 'Z')
            
        {
            
            [s appendFormat:@"%@",[string substringWithRange:NSMakeRange(i,1)]];
            
            
            
        }
        
        else
            
        {
            
            [s appendFormat:@"\\u%x",[string characterAtIndex:i]];
            
        }
        
    }
    
    return s;
    
}

//验证手机号码
+ (BOOL)validateMobile:(NSString *)mobileNum {
    /**
     * 手机号码
     * 移动：134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
     * 联通：130,131,132,152,155,156,185,186
     * 电信：133,1349,153,180,189
     */
    NSString * MOBILE = @"^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$";
    /**
     10         * 中国移动：China Mobile
     11         * 134[0-8],135,136,137,138,139,150,151,157,158,159,182,187,188
     12         */
    NSString * CM = @"^1(34[0-8]|(3[5-9]|5[017-9]|8[278])\\d)\\d{7}$";
    /**
     15         * 中国联通：China Unicom
     16         * 130,131,132,152,155,156,185,186
     17         */
    NSString * CU = @"^1(3[0-2]|5[256]|8[56])\\d{8}$";
    /**
     20         * 中国电信：China Telecom
     21         * 133,1349,153,180,189
     22         */
    NSString * CT = @"^1((33|53|8[09])[0-9]|349)\\d{7}$";
    /**
     25         * 大陆地区固话及小灵通
     26         * 区号：010,020,021,022,023,024,025,027,028,029
     27         * 号码：七位或八位
     28         */
    // NSString * PHS = @"^0(10|2[0-5789]|\\d{3})\\d{7,8}$";
    NSPredicate *regextestmobile = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", MOBILE];
    NSPredicate *regextestcm = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", CM];
    NSPredicate *regextestcu = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", CU];
    NSPredicate *regextestct = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", CT];
    if (([regextestmobile evaluateWithObject:mobileNum] == YES)
        || ([regextestcm evaluateWithObject:mobileNum] == YES)
        || ([regextestct evaluateWithObject:mobileNum] == YES)
        || ([regextestcu evaluateWithObject:mobileNum] == YES))
    {
        return YES;
    }
    else
    {
        return NO;
    }
}

//验证身份证号码合法性
+ (BOOL)verifyIDCardNumber:(NSString *)value
{
    value = [value stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    if ([value length] != 18) {
        return NO;
    }
    NSString *mmdd = @"(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8])))";
    NSString *leapMmdd = @"0229";
    NSString *year = @"(19|20)[0-9]{2}";
    NSString *leapYear = @"(19|20)(0[48]|[2468][048]|[13579][26])";
    NSString *yearMmdd = [NSString stringWithFormat:@"%@%@", year, mmdd];
    NSString *leapyearMmdd = [NSString stringWithFormat:@"%@%@", leapYear, leapMmdd];
    NSString *yyyyMmdd = [NSString stringWithFormat:@"((%@)|(%@)|(%@))", yearMmdd, leapyearMmdd, @"20000229"];
    NSString *area = @"(1[1-5]|2[1-3]|3[1-7]|4[1-6]|5[0-4]|6[1-5]|82|[7-9]1)[0-9]{4}";
    NSString *regex = [NSString stringWithFormat:@"%@%@%@", area, yyyyMmdd  , @"[0-9]{3}[0-9Xx]"];
    
    NSPredicate *regexTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", regex];
    if (![regexTest evaluateWithObject:value]) {
        return NO;
    }
    return TRUE;
}

//从userdefault中获取特定值
+ (NSString *)getValueFromUserDefaultWithKey:(NSString *)key
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    NSString *resultString;
    
    if ([userDefault objectForKey:key] != nil) {
        NSString *String = [userDefault stringForKey:key];
        resultString = String;
    }
    return resultString;
}
@end
