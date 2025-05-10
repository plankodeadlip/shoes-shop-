package com.example.mophoneapp11.untils;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.OutputStreamWriter;

public class ImageUploader {
    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(String error);
    }

    public static void uploadToImgBB(Context context, Uri imageUri, String apiKey, UploadCallback callback) {
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... voids) {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                    String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                    URL url = new URL("https://api.imgbb.com/1/upload?key=" + apiKey);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);

                    String data = "image=" + encodedImage;
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(data);
                    writer.flush();
                    writer.close();

                    InputStream responseStream = conn.getInputStream();
                    ByteArrayOutputStream responseBaos = new ByteArrayOutputStream();
                    while ((bytesRead = responseStream.read(buffer)) != -1) {
                        responseBaos.write(buffer, 0, bytesRead);
                    }

                    JSONObject json = new JSONObject(responseBaos.toString());
                    String imageUrl = json.getJSONObject("data").getString("url");
                    return imageUrl;

                } catch (Exception e) {
                    Log.e("UploadError", "Error uploading image: " + e.getMessage());
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    callback.onFailure("Không thể tải ảnh lên ImgBB.");
                }
            }
        }.execute();
    }
}
