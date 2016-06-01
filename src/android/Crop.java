package cordova.plugins.pickcrop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import org.apache.cordova.CordovaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Builder for crop Intents and utils for handling result
 */
public class Crop {

    public static final int REQUEST_CROP = 6709;
    public static final int REQUEST_PICK = 9162;
    public static final int REQUEST_CAMERA=2435;
    public static final int RESULT_ERROR = 404;

    interface Extra {
        String ASPECT_X = "aspect_x";
        String ASPECT_Y = "aspect_y";
        String MAX_X = "max_x";
        String MAX_Y = "max_y";
        String ERROR = "error";
    }

    private Intent cropIntent;

    /**
     * Create a crop Intent builder with source and destination image Uris
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    public static Crop of(Uri source, Uri destination) {
        return new Crop(source, destination);
    }

    private Crop(Uri source, Uri destination) {
        cropIntent = new Intent();
        cropIntent.setData(source);
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, destination);
    }

    /**
     * Set fixed aspect ratio for crop area
     *
     * @param x Aspect X
     * @param y Aspect Y
     */
    public Crop withAspect(int x, int y) {
        cropIntent.putExtra(Extra.ASPECT_X, x);
        cropIntent.putExtra(Extra.ASPECT_Y, y);
        return this;
    }

    /**
     * Crop area with fixed 1:1 aspect ratio
     */
    public Crop asSquare() {
        cropIntent.putExtra(Extra.ASPECT_X, 1);
        cropIntent.putExtra(Extra.ASPECT_Y, 1);
        return this;
    }

    /**
     * Set maximum crop size
     *
     * @param width  Max width
     * @param height Max height
     */
    public Crop withMaxSize(int width, int height) {
        cropIntent.putExtra(Extra.MAX_X, width);
        cropIntent.putExtra(Extra.MAX_Y, height);
        return this;
    }

    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    public void start(Activity activity) {
        start(activity, REQUEST_CROP);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }
    /**
     * Send the crop Intent from an Activity with a custom request code
     * by CordovaPlugin
     * @param plugin
     */
    public void start(CordovaPlugin plugin) {
        plugin.cordova.startActivityForResult(plugin, getIntent(plugin.cordova.getActivity()), REQUEST_CROP);
    }

    /**
     * Send the crop Intent from a Fragment
     *
     * @param context  Context
     * @param fragment Fragment to receive result
     */
    public void start(Context context, Fragment fragment) {
        start(context, fragment, REQUEST_CROP);
    }


    /**
     * Send the crop Intent with a custom request code
     *
     * @param context     Context
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void start(Context context, Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    /**
     * Get Intent to start crop Activity
     *
     * @param context Context
     * @return Intent for CropImageActivity
     */
    public Intent getIntent(Context context) {
        cropIntent.setClass(context, CropImageActivity.class);
        return cropIntent;
    }

    /**
     * Retrieve URI for cropped image, as set in the Intent builder
     *
     * @param result Output Image URI
     */
    public static Uri getOutput(Intent result) {
        return result.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
    }

    /**
     * Retrieve error that caused crop to fail
     *
     * @param result Result Intent
     * @return Throwable handled in CropImageActivity
     */
    public static Throwable getError(Intent result) {
        return (Throwable) result.getSerializableExtra(Extra.ERROR);
    }

    /**
     * Pick image from an Activity
     *
     * @param activity Activity to receive result
     */
    public static void pickImage(Activity activity) {
        pickImage(activity, REQUEST_PICK);
    }

    /**
     * Pick image from a Fragment
     *
     * @param context  Context
     * @param fragment Fragment to receive result
     */
    public static void pickImage(Context context, Fragment fragment) {
        pickImage(context, fragment, REQUEST_PICK);
    }

    /**
     * Pick image from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public static void pickImage(Activity activity, int requestCode) {
        try {
            activity.startActivityForResult(getImagePicker(), requestCode);
        } catch (ActivityNotFoundException e) {
            showImagePickerError(activity);
        }
    }

    /**
     * Pick image from an CordovaPlugin with a custom request code
     * @param plugin CordovaPlugin
     */
    public static void pickImage(CordovaPlugin plugin){
        try {
            plugin.cordova.startActivityForResult(plugin, getImagePicker(), REQUEST_PICK);
        } catch (ActivityNotFoundException e) {
            showImagePickerError(plugin.cordova.getActivity());
        }
    }

    /**
     * Pick image from a Fragment with a custom request code
     *
     * @param context     Context
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void pickImage(Context context, Fragment fragment, int requestCode) {
        try {
            fragment.startActivityForResult(getImagePicker(), requestCode);
        } catch (ActivityNotFoundException e) {
            showImagePickerError(context);
        }
    }

    private static Intent getImagePicker() {
        return new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
    }

    private static void showImagePickerError(Context context) {
        Toast.makeText(context, "无效的图片", Toast.LENGTH_SHORT).show();
    }

    /**
     * get base64 string
     * @param uri
     * @return
     */
    public static String toBase64(Context context,Uri uri,int quality){
        byte[] bytes=toBytes(context,uri,quality);
        if(bytes==null)return null;
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static String toBase64(Context context,Bitmap bitmap,int quality){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos); //bm is the bitmap object
        byte[] bytes= baos.toByteArray();
        return Base64.encodeToString(bytes,Base64.NO_WRAP);
    }

    private static byte[] toBytes(Context context,Uri uri,int quality){
        Bitmap bm = null;
        try {
            bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baos); //bm is the bitmap object
        return baos.toByteArray();
    }

    /**
     *
     * @param context
     * @param uri
     * @param maxHeight
     * @return
     */
    public static Bitmap zoomImage(Context context,Uri uri,int maxHeight){
        File file=CropUtil.getFromMediaUri(context,context.getContentResolver(),uri);
        BitmapFactory.Options options=new BitmapFactory.Options();
        Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath(),options);
        int outW=options.outWidth*maxHeight/options.outHeight;
       return Bitmap.createScaledBitmap(bitmap,outW,maxHeight,true);
    }
}
