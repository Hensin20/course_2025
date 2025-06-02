package com.example.project1;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadImage {

    private final Context context;
    private final OkHttpClient client;
    public static final int IMAGE_PICK_CODE = 1000;
    private Consumer<String> uploadCallback;

    public UploadImage(Context context, OkHttpClient client) {
        this.context = context;
        this.client = client;
    }

    public void selectImage(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, Consumer<String> callback) {
        if (requestCode == IMAGE_PICK_CODE && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri, callback);
        }
    }

    private void uploadImage(Uri imageUri, Consumer<String> onSuccess) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String fileName = getFileName(imageUri);

            if (fileName == null) {
                showToast("❌ Не вдалося отримати ім’я файлу!");
                return;
            }

            String nameWithoutExtension = fileName.contains(".")
                    ? fileName.substring(0, fileName.lastIndexOf('.'))
                    : fileName;

            InputStream inputStream = contentResolver.openInputStream(imageUri);
            if (inputStream == null) {
                showToast("❌ Неможливо відкрити файл!");
                return;
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) {
                showToast("❌ Неможливо декодувати зображення!");
                return;
            }

            ByteArrayOutputStream pngStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, pngStream);
            byte[] pngBytes = pngStream.toByteArray();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", nameWithoutExtension + ".png",
                            RequestBody.create(pngBytes, MediaType.parse("image/png")))
                    .build();

            Request request = new Request.Builder()
                    .url(ApiClient.BASE_URL + "/api/workouts/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String uploadedName = nameWithoutExtension;
                        onSuccess.accept(uploadedName);
                        Log.d("UPLOAD_SUCCESS", "Файл завантажено: " + uploadedName);
                    } else {
                        showToast("❌ Помилка завантаження файлу на сервер!");
                        Log.e("UPLOAD_ERROR", "Сервер відповів: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    showToast("⚠ Помилка мережі при завантаженні!");
                    Log.e("UPLOAD_ERROR", "Exception: ", e);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            showToast("❌ Помилка обробки зображення!");
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void showToast(String message) {
        android.os.Handler handler = new android.os.Handler(context.getMainLooper());
        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}
