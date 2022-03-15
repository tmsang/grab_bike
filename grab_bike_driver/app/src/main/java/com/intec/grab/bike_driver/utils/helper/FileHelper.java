package com.intec.grab.bike_driver.utils.helper;

import static com.intec.grab.bike_driver.utils.api.Callback.call;
import static com.intec.grab.bike_driver.utils.api.Callback.callInUI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;

import androidx.core.app.ActivityCompat;

import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.messages.MessagesActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.log.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileHelper {

    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    private Activity activity;
    private Context context;

    public FileHelper(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public void Save(String fileOnlyName, String value) {
        // Requesting Permission to access External Storage
        ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
                },
                EXTERNAL_STORAGE_PERMISSION_CODE);

        // getExternalStoragePublicDirectory() represents root of external storage, we are using DOWNLOADS
        // We can use following directories: MUSIC, PODCASTS, ALARMS, RINGTONES, NOTIFICATIONS, PICTURES, MOVIES
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Storing the data in file with name as geeksData.txt
        File file = new File(folder, fileOnlyName);
        writeTextData(file, value);

        //i("Done" + file.getAbsolutePath());
    }

    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    private void uploadFile(Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = getFile(fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(context.getContentResolver().getType(fileUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        // finally, execute the request
        SharedService.MessageApi(Constants.API_NET, sslSettings)
                .Upload(header, description, body)
                .enqueue(call((json) -> {
                            // TODO: refactor 0

                        },
                        (error) -> {
                            // TODO: ...
                        }
                ));
    }
    */

    public File getFile(Uri uri) {
        if (uri != null) {
            String path = "";       // getPath(context, uri);
            if (path != null && isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }

    public boolean isLocal(String url) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            return true;
        }
        return false;
    }


}
