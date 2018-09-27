
package com.thebylito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.util.List;

public class RNIntentLauncherModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private static final int REQUEST_CODE = 12;
    private static final String FILE_NAME = "fileName";
    private final ReactApplicationContext reactContext;
    Promise promise;

    public RNIntentLauncherModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
        this.reactContext = reactContext;
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.size() > 0;
    }

    @Override
    public String getName() {
        return "RNIntentLauncher";
    }

    @ReactMethod
    public void startActivity(ReadableMap params, final Promise promise) {
        this.promise = promise;
        Intent intent;
        String fileName = null;
        if (params.hasKey(FILE_NAME)) {
            fileName = params.getString(FILE_NAME);
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri;
        contentUri = Uri.fromFile(file);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkURI = FileProvider.getUriForFile(reactContext, reactContext.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(apkURI, "application/octet-stream");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(contentUri, "application/octet-stream");
        }

        if(isIntentAvailable(reactContext, intent)){
        reactContext.startActivityForResult(intent, REQUEST_CODE, null);
        }else{
            WritableMap map = Arguments.createMap();
            this.promise.reject("ERROR", "Nenhum app instalado para abrir esse tipo de arquivo.");
            return;
        }

    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
        if (requestCode != REQUEST_CODE) {
            return;
        }
        WritableMap params = Arguments.createMap();
        if (intent != null) {
            params.putInt("resultCode", resultCode);

            Uri data = intent.getData();
            if (data != null) {
                params.putString("data", data.toString());
            }
            Bundle extras = intent.getExtras();
            if (extras != null) {
                params.putMap("extra", Arguments.fromBundle(extras));
            }
        }
        this.promise.resolve(params);
    }
    @Override
    public void onNewIntent(Intent intent) {
    }
}