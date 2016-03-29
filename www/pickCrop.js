var exec=require('cordova/exec');
module.exports={
    /**
     * just pick a image ,dont crop it
     * @param {[[Type]]} height  [[set the result image's height]]
     * @param {[[Type]]} success [[success callback function]]
     * @param {[[Type]]} error   [[error callback function]]
     */
    pick:function(height,success,error){
        exec(success,error,'PickCrop','pick',[height]);
    },
     /**
     *  pick a image and crop it
     * @param {[[Type]]} height  [[set the result image's height]]
     * @param {[[Type]]} success [[success callback function]]
     * @param {[[Type]]} error   [[error callback function]]
     */
    crop:function(height,success,error){
        exec(success,error,'PickCrop','crop',[height]);
    }
}