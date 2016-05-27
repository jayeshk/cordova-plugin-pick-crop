var exec=require('cordova/exec');
var _defaultHeight=400,
    _defaultQuality=100;
module.exports={
    /**
     * just pick a image ,dont crop it
     * @param {[[Type]]} height  [[set the result image's height]]
     * @param {[[Type]]} success [[success callback function]]
     * @param {[[Type]]} error   [[error callback function]]
     */
    pick:function(params,success,error){
        var param=[]; 
        if(params){
            if(params.height){
                param.push(params.height);
            }else{
                param.push(_defaultHeight);
            }
            if(params.quality){
                param.push(params.quality);
            }else{
                param.push(_defaultQuality);
            }
        }else{
            param.push(_defaultHeight);
            param.push(_defaultQuality);
        }
        exec(success,error,'PickCrop','pick',param);
    },
     /**
     *  pick a image and crop it
     * @param {[[Type]]} height  [[set the result image's height]]
     * @param {[[Type]]} success [[success callback function]]
     * @param {[[Type]]} error   [[error callback function]]
     */
    crop:function(params,success,error){
        var param=[];
        if(params){
            if(params.height){
                param.push(params.height);
            }else{
                param.push(_defaultHeight);
            }
            if(params.quality){
                param.push(params.quality);
            }else{
                param.push(_defaultQuality);
            }
        }else{
            param.push(_defaultHeight);
            param.push(_defaultQuality);
        }
        exec(success,error,'PickCrop','crop',param);
    }
}