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
    
    
    // NSLog(@"Cordova iOS Cache.clear() called.");
    // pickerController *pc = [[pickerController alloc]init];
    //获取方式1：通过相册（呈现全部相册），UIImagePickerControllerSourceTypePhotoLibrary
    //获取方式2，通过相机，UIImagePickerControllerSourceTypeCamera
    //获取方方式3，通过相册（呈现全部图片），UIImagePickerControllerSourceTypeSavedPhotosAlbum
    // pc.sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;//方式1
    // //允许编辑，即放大裁剪
    // pc.allowsEditing = YES;
    // //自代理
    // pc.delegate = self;
    // self.callbackId = command.callbackId;
    // NSLog(@"-------------callbackId-------------------------");
    // NSLog(self.callbackId);
//
//    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
//                                                      messageAsString:@"FFF"];
    
//    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
//    CDVPluginResult *result=[CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
//    [result setKeepCallbackAsBool:YES];
//    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    
    //    [self.viewController addChildViewController:childView];
    // [self.viewController presentViewController:pc animated:YES completion:NULL];
    // 
     self.hasPendingOperation = YES;
    
    __weak CDVCamera* weakSelf = self;

    [self.commandDelegate runInBackground:^{
        
        CDVPictureOptions* pictureOptions = [CDVPictureOptions createFromTakePictureArguments:command];
        pictureOptions.popoverSupported = [weakSelf popoverSupported];
        pictureOptions.usesGeolocation = [weakSelf usesGeolocation];
        pictureOptions.cropToSize = NO;
        
        BOOL hasCamera = [UIImagePickerController isSourceTypeAvailable:pictureOptions.sourceType];
        if (!hasCamera) {
            NSLog(@"Camera.getPicture: source type %lu not available.", (unsigned long)pictureOptions.sourceType);
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No camera available"];
            [weakSelf.commandDelegate sendPluginResult:result callbackId:command.callbackId];
            return;
        }

        // Validate the app has permission to access the camera
        if (pictureOptions.sourceType == UIImagePickerControllerSourceTypeCamera && [AVCaptureDevice respondsToSelector:@selector(authorizationStatusForMediaType:)]) {
            AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
            if (authStatus == AVAuthorizationStatusDenied ||
                authStatus == AVAuthorizationStatusRestricted) {
                // If iOS 8+, offer a link to the Settings app
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wtautological-pointer-compare"
                NSString* settingsButton = (&UIApplicationOpenSettingsURLString != NULL)
                    ? NSLocalizedString(@"Settings", nil)
                    : nil;
#pragma clang diagnostic pop

                // Denied; show an alert
                dispatch_async(dispatch_get_main_queue(), ^{
                    [[[UIAlertView alloc] initWithTitle:[[NSBundle mainBundle]
                                                         objectForInfoDictionaryKey:@"CFBundleDisplayName"]
                                                message:NSLocalizedString(@"Access to the camera has been prohibited; please enable it in the Settings app to continue.", nil)
                                               delegate:weakSelf
                                      cancelButtonTitle:NSLocalizedString(@"OK", nil)
                                      otherButtonTitles:settingsButton, nil] show];
                });
            }
        }

        CDVCameraPicker* cameraPicker = [CDVCameraPicker createFromPictureOptions:pictureOptions];
        weakSelf.pickerController = cameraPicker;
        
        cameraPicker.delegate = weakSelf;
        cameraPicker.callbackId = command.callbackId;
        // we need to capture this state for memory warnings that dealloc this object
        cameraPicker.webView = weakSelf.webView;
        
        // Perform UI operations on the main thread
        dispatch_async(dispatch_get_main_queue(), ^{
            // If a popover is already open, close it; we only want one at a time.
            if (([[weakSelf pickerController] pickerPopoverController] != nil) && [[[weakSelf pickerController] pickerPopoverController] isPopoverVisible]) {
                [[[weakSelf pickerController] pickerPopoverController] dismissPopoverAnimated:YES];
                [[[weakSelf pickerController] pickerPopoverController] setDelegate:nil];
                [[weakSelf pickerController] setPickerPopoverController:nil];
            }

            if ([weakSelf popoverSupported] && (pictureOptions.sourceType != UIImagePickerControllerSourceTypeCamera)) {
                if (cameraPicker.pickerPopoverController == nil) {
                    cameraPicker.pickerPopoverController = [[NSClassFromString(@"UIPopoverController") alloc] initWithContentViewController:cameraPicker];
                }
                [weakSelf displayPopover:pictureOptions.popoverOptions];
                weakSelf.hasPendingOperation = NO;
            } else {
                [weakSelf.viewController presentViewController:cameraPicker animated:YES completion:^{
                    weakSelf.hasPendingOperation = NO;
                }];
            }
        });
    }];
    
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