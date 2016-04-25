package cordova.plugins.pickcrop;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

/**
 * Create by Huang Li
 */
public class PickCropPlugin extends CordovaPlugin implements View.OnClickListener{
    private static final int CAMERA_REQUEST_CODE = 862;
    /**
     * true 表示只选择图片并不，裁剪图片
     */
    private boolean justPick=true;
    /**
     *  set the result image height,default 400 for transmission
     */
    private int targetHeight=400;

    private CallbackContext callbackContext;
    private String action;
    private PopSelector selector;
    /**
     * Save path of the camera picture
     */
    private Uri takePhotoUri;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext=callbackContext;
        PluginResult result=new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
        this.action=action;
        if(args.length()==1)targetHeight=args.getInt(0);
        showPop();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            if(justPick){
                Bitmap bm=Crop.zoomImage(cordova.getActivity(),intent.getData(),targetHeight);
                String r=Crop.toBase64(cordova.getActivity(), bm);
                bm.recycle();
                if(r==null){
                    this.callbackContext.error("Convert to base64 is error");
                }else {
                    this.callbackContext.success(r);
                }
            }else {
                beginCrop(intent.getData());
            }
        } else if (requestCode == Crop.REQUEST_CROP&&resultCode==Activity.RESULT_OK) {
            handleCrop(resultCode, intent);
        }else if(requestCode==Crop.REQUEST_CAMERA&&resultCode==Activity.RESULT_OK){
            if(justPick) {
                Bitmap bm = Crop.zoomImage(cordova.getActivity(), takePhotoUri, targetHeight);
                String r = Crop.toBase64(cordova.getActivity(), bm);
                bm.recycle();
                if (r == null) {
                    this.callbackContext.error("Convert to base64 is error");
                } else {
                    this.callbackContext.success(r);
                }
            }else {
                beginCrop(takePhotoUri);
            }
        }else {
            this.callbackContext.error("user no select");
        }
    }

    /**
     *
     * @param source
     */
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(cordova.getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).withMaxSize(targetHeight,targetHeight).asSquare().start(this);
    }

    /**
     * 剪切成功后的回调
     * @param resultCode
     * @param result
     */
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

    private void showPop(){
        if(selector==null)selector=new PopSelector(cordova.getActivity(),this);
        if(selector.isShowing())return;
        selector.showAtLocation(cordova.getActivity().getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 从照相机获取源图片
     */
    private void takePhoto(){
        takePhotoUri = Uri.fromFile(new File(cordova.getActivity().getExternalCacheDir(), "/taked.jpg"));
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri); //指定图片输出地址
        cordova.startActivityForResult(this, intent, Crop.REQUEST_CAMERA);
    }
    private void doIt(){
        Crop.pickImage(this);
    }

    private void checkPermission(){
        if (!cordova.hasPermission(Manifest.permission.CAMERA)) {
            cordova.requestPermissions(this, CAMERA_REQUEST_CODE,
                    new String[]{Manifest.permission.CAMERA}
            );
        }else {
            takePhoto();
        }
    }

    private void goToSettings() {
        int id_OK=FakeR.getId(cordova.getActivity(),"string","crop__done");
        int id_Cancel=FakeR.getId(cordova.getActivity(),"string","crop__cancel");
        AlertDialog.Builder builder=new AlertDialog.Builder(cordova.getActivity());
        builder.setMessage(FakeR.getId(cordova.getActivity(),"string","crop__no_permission"));
        builder.setPositiveButton(id_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + cordova.getActivity().getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cordova.getActivity().startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton(id_Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        for(int r:grantResults){
            if(r == PackageManager.PERMISSION_DENIED)
            {
                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goToSettings();
                    }
                });
                return;
            }
        }
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                takePhoto();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int camera=FakeR.getId(cordova.getActivity(),"id","btn_take_photo");
        int gallery=FakeR.getId(cordova.getActivity(),"id","btn_pick_photo");
        if("pick".equals(action)){
            justPick=true;
        }else if ("crop".equals(action)){
            justPick=false;
        }
        if(v.getId()==camera){
            checkPermission();
        }else if(v.getId()==gallery){
            doIt();
        }else {
            callbackContext.error("user no select");
        }
        selector.dismiss();
    }

}