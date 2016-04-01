var exec=require('cordova/exec');
module.exports={
    /**
     * just pick a image ,dont crop it
     * @param {[[Type]]} height  [[set the result image's height]]
     * @param {[[Type]]} success [[success callback function]]
     * @param {[[Type]]} error   [[error callback function]]
     */
    pick:function(height,success,error){
        var param=[];
        if(height)param.push(height);
        exec(success,error,'PickCrop','pick',param);
    },
     /**
     *  pick a image and crop it
     * @param {[[Type]]} height  [[set the result image's height]]
     * @param {[[Type]]} success [[success callback function]]
     * @param {[[Type]]} error   [[error callback function]]
     */
    crop:function(height,success,error){
        var param=[];
        if(height)param.push(height);
        exec(success,error,'PickCrop','crop',param);
    }
}