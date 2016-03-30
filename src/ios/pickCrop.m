/*
 Copyright 2014 Modern Alchemists OG
 
 Licensed under MIT.
 
 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */


#import "pickCrop.h"

@implementation pickCrop

@synthesize command;
@synthesize callbackId;

/**
 *选择图片
 *
 **/

- (void)pick:(CDVInvokedUrlCommand*)command
{
    NSLog(@"查看图片");
}
/**
 *裁剪图片
 *
 **/
- (void)crop:(CDVInvokedUrlCommand*)command
{
    
    
    NSLog(@"Cordova iOS Cache.clear() called.");
    pickerController *pc = [[pickerController alloc]init];
    // 获取方式1：通过相册（呈现全部相册），UIImagePickerControllerSourceTypePhotoLibrary
    // 获取方式2，通过相机，UIImagePickerControllerSourceTypeCamera
    // 获取方方式3，通过相册（呈现全部图片），UIImagePickerControllerSourceTypeSavedPhotosAlbum
    pc.sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;//方式1
    //允许编辑，即放大裁剪
    pc.allowsEditing = YES;
    //自代理
    pc.delegate = self;
    self.callbackId = command.callbackId;
    NSLog(@"-------------callbackId-------------------------");
    NSLog(self.callbackId);
//
//    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
//                                                      messageAsString:@"FFF"];
    
//    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
//    CDVPluginResult *result=[CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
//    [result setKeepCallbackAsBool:YES];
//    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    
    //    [self.viewController addChildViewController:childView];
    [self.viewController presentViewController:pc animated:YES completion:NULL];
    // 
     
    
}
//PickerImage完成后的代理方法
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info{
    //裁剪完成 关闭当前页面
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    
    //定义一个newPhoto，用来存放我们选择的图片。
    UIImage *newPhoto = [info objectForKey:@"UIImagePickerControllerEditedImage"];
    
//    [self stringImage:newPhoto];
    NSData *mydata=UIImageJPEGRepresentation(newPhoto, 0.4);
    NSString *pictureDataString = [mydata base64Encoding];
    NSLog(@"-------------开始转换-------------------------");
    NSLog(self.callbackId);
    NSLog(@"%d",pictureDataString.length);
    
    
    if (self.callbackId == nil) {
        return;
    }
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsString:pictureDataString];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
    self.callbackId = nil;
    
    
    
}
//图片转换base64
- (void)stringImage:(UIImage *)img{
   
    
    
}

@end