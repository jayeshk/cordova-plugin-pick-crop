package cordova.plugins.pickcrop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

public class PickCropPlugin extends CordovaPlugin{
    /**
     * true 表示只选择图片并不，裁剪图片
     */
    private boolean justPick=true;
    /**
     *  set the result image height,default 500 for transmission
     */
    private int targetHeight=500;

    private CallbackContext callbackContext;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext=callbackContext;
        PluginResult result=new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
        if("pick".equals(action)){
            justPick=true;
            if(args.length()==1)targetHeight=args.getInt(0);
            Crop.pickImage(this);
        }else if ("crop".equals(action)){
            justPick=false;
            if(args.length()==1)targetHeight=args.getInt(0);
            Crop.pickImage(this);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            if(justPick){
                String r=Crop.toBase64(cordova.getActivity(), intent.getData());
                if(r==null){
                    this.callbackContext.error("Convert to base64 is error");
                }else {
                    this.callbackContext.success(r);
                }
            }else {
                beginCrop(intent.getData());
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, intent);
        }else {
            this.callbackContext.error("user no select");
        }
    }
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(cordova.getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).withMaxSize(targetHeight,targetHeight).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            String r=Crop.toBase64(cordova.getActivity(), Crop.getOutput(result));
            if(r==null){
                this.callbackContext.error("Convert to base64 is error");
            }else {
                this.callbackContext.success(r);
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            this.callbackContext.error(Crop.getError(result).getMessage());
        }
    }
}