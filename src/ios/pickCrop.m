
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
    NSLog(@"Cordova iOS Cache.clear() called.%@",[command.arguments objectAtIndex:0]);
//    self.hight = ;
    self.height = [[command.arguments objectAtIndex:0]intValue];
    if (self.height == nil) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                          messageAsString:@"参数错误!"];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
        return;
    }
    
    self.callbackId = command.callbackId;
    [self alterHeadPortrait];
}
-(void)alterHeadPortrait{
    
    //初始化提示框
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:nil preferredStyle:UIAlertControllerStyleActionSheet];
    //按钮：从相册选择，类型：UIAlertActionStyleDefault
    [alert addAction:[UIAlertAction actionWithTitle:@"从相册选择" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        //初始化UIImagePickerController
        UIImagePickerController *PickerImage = [[UIImagePickerController alloc]init];
        //获取方式1：通过相册（呈现全部相册），UIImagePickerControllerSourceTypePhotoLibrary
        //获取方式2，通过相机，UIImagePickerControllerSourceTypeCamera
        //获取方法3，通过相册（呈现全部图片），UIImagePickerControllerSourceTypeSavedPhotosAlbum
        PickerImage.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        //允许编辑，即放大裁剪
        PickerImage.allowsEditing = YES;
        //自代理
        PickerImage.delegate = self;
        //页面跳转
        [self.viewController presentViewController:PickerImage animated:YES completion:nil];
    }]];
    //按钮：拍照，类型：UIAlertActionStyleDefault
    [alert addAction:[UIAlertAction actionWithTitle:@"拍照" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action){
        /**
         其实和从相册选择一样，只是获取方式不同，前面是通过相册，而现在，我们要通过相机的方式
         */
        UIImagePickerController *PickerImage = [[UIImagePickerController alloc]init];
        //获取方式:通过相机
        PickerImage.sourceType = UIImagePickerControllerSourceTypeCamera;
        PickerImage.allowsEditing = YES;
        PickerImage.delegate = self;
        [self.viewController presentViewController:PickerImage animated:YES completion:nil];
    }]];
    //按钮：取消，类型：UIAlertActionStyleCancel
    [alert addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil]];
    [self.viewController presentViewController:alert animated:YES completion:nil];
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
    [self.viewController presentViewController:pc animated:YES completion:NULL];
}
//PickerImage完成后的代理方法
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info{
    //裁剪完成 关闭当前页面
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    
    //定义一个newPhoto，用来存放我们选择的图片。
    UIImage *newPhoto = [info objectForKey:@"UIImagePickerControllerEditedImage"];
    CGFloat height= (CGFloat)(self.height);
    UIImage *newd = [self scaleToSize:newPhoto size:CGSizeMake(height,height)];
    
    NSData *mydata=UIImageJPEGRepresentation(newd, 0.4);
    NSString *pictureDataString = [mydata base64Encoding];
    if (self.callbackId == nil) {
        return;
    }
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                      messageAsString:pictureDataString];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
    self.callbackId = nil;
    
    
    
}
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker{
    
    NSString *pictureDataString = @"失败";
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                      messageAsString:pictureDataString];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callbackId];
    self.callbackId = nil;
    NSLog(@"相册取消");
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
}
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated{
    viewController.contentSizeForViewInPopover = CGSizeMake(800, 800);
    
}

- (UIImage *)scaleToSize:(UIImage *)img size:(CGSize)size{
    // 创建一个bitmap的context
    // 并把它设置成为当前正在使用的context
    UIGraphicsBeginImageContext(size);
    // 绘制改变大小的图片
    [img drawInRect:CGRectMake(0, 0, size.width, size.height)];
    // 从当前context中创建一个改变大小后的图片
    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    // 使当前的context出堆栈
    UIGraphicsEndImageContext();
    // 返回新的改变大小后的图片
    return scaledImage; 
}
@end