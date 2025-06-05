package com.example.project1.trenning;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project1.ApiClient;
import com.example.project1.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_training_lessons extends Fragment {

    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList = new ArrayList<>();
    private String workoutId, title, description, duration, exercise, imagePath;
    private TextView tvTitle, tvDescription, tvDurations, tvExcercise;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private String uploadedImageName = null;

    private static final int IMAGE_PICK_CODE = 1001;
    private BottomSheetDialog dialog;
    private OkHttpClient client = new OkHttpClient();
    private EditText edtTitle, edtVideoUrl, edtDuration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            workoutId = getArguments().getString("workoutId");
            Log.e("Fragment_training_lessons", "отримано workoutId" + workoutId);
            title = getArguments().getString("title");
            description = getArguments().getString("description");
            duration = getArguments().getString("duration");
            exercise = getArguments().getString("exercise");
            imagePath = getArguments().getString("imagePath");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trening_lessons, container, false);

        Button button_add_lessons = view.findViewById(R.id.button_add_lessons);
        button_add_lessons.setOnClickListener(v -> showAddExerciseDialog(null));

        tvTitle = view.findViewById(R.id.titleText);
        tvDescription = view.findViewById(R.id.excercise_Text);
        tvDurations = view.findViewById(R.id.durationText);
        tvExcercise = view.findViewById(R.id.excerciseText);
        imageView = view.findViewById(R.id.imageView_pic);
        recyclerView = view.findViewById(R.id.list_lesson);

        SharedPreferences prefs = getActivity().getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userRole = prefs.getString("userRole", "user"); // ✅ Отримуємо роль
        if(!userRole.equals("admin")){
            button_add_lessons.setVisibility(View.GONE);
        } else {
            button_add_lessons.setVisibility(View.VISIBLE);
        }

        tvTitle.setText(title);
        tvDescription.setText(description);
        tvDurations.setText(duration);
        tvExcercise.setText(exercise);

        Glide.with(requireContext())
                .load(ApiClient.BASE_URL + "/images/" + imagePath)
                .placeholder(R.drawable.kardio)
                .into(imageView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        exerciseAdapter = new ExerciseAdapter(exerciseList, requireContext(), this); // ✅ Передаємо фрагмент

        recyclerView.setAdapter(exerciseAdapter);

        exerciseAdapter.setClickListener(exercise -> {
            String videoUrl = exercise.getVideoUrl();
            if (videoUrl != null && !videoUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");

                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                    startActivity(fallbackIntent);
                }
            }
        });

        fetchExercises();

        return view;
    }

    public void showAddExerciseDialog(Exercise exercise) {
        dialog = new BottomSheetDialog(getContext());
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_exercise, null);
        dialog.setContentView(sheetView);

        edtTitle = sheetView.findViewById(R.id.editTextTitle);
        edtVideoUrl = sheetView.findViewById(R.id.editTextVideoUrl);
        edtDuration = sheetView.findViewById(R.id.editTextDuration);
        Button btnSelectImage = sheetView.findViewById(R.id.buttonSelectImage);
        Button btnSave = sheetView.findViewById(R.id.buttonSaveExercise);

        btnSelectImage.setOnClickListener(v -> selectImage());

        // ✅ Якщо exercise == null, це новий урок, інакше заповнюємо поля для редагування
        if (exercise != null) {
            edtTitle.setText(exercise.getTitle());
            edtVideoUrl.setText(exercise.getVideoUrl());
            edtDuration.setText(exercise.getDurationSeconds());
            uploadedImageName = exercise.getPreviewImageUrl();
        }

        btnSave.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String duration = edtDuration.getText().toString().trim();
            String videoUrl = edtVideoUrl.getText().toString().trim();

            if (title.isEmpty() || duration.isEmpty() || videoUrl.isEmpty()) {
                Toast.makeText(getContext(), "❗Заповніть всі поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uploadedImageName == null) {
                Toast.makeText(getContext(), "⏳ Завантажте зображення!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (exercise != null) {
                updateExercise(exercise.getExerciseId(), title, videoUrl, duration, uploadedImageName); // ✅ Викликаємо редагування
            } else {
                uploadExercise(title, videoUrl, duration, uploadedImageName, workoutId); // ✅ Додаємо новий урок
            }

            dialog.dismiss();
        });

        dialog.show();
    }


    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        try {
            String fileName = getFileName(imageUri);
            if (fileName == null) {
                Toast.makeText(getContext(), "❌ Не вдалося отримати ім’я файлу!", Toast.LENGTH_SHORT).show();
                return;
            }

            String nameWithoutExt = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
                Toast.makeText(getContext(), "❌ Неможливо прочитати зображення!", Toast.LENGTH_SHORT).show();
                return;
            }

            ByteArrayOutputStream pngStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, pngStream);
            byte[] pngBytes = pngStream.toByteArray();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", nameWithoutExt + ".png", RequestBody.create(pngBytes, MediaType.parse("image/png")))
                    .build();

            Request request = new Request.Builder()
                    .url(ApiClient.BASE_URL + "/api/workouts/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        uploadedImageName = nameWithoutExt;
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "✅ Зображення завантажено", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "❌ Помилка завантаження зображення", Toast.LENGTH_SHORT).show()
                        );
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "⚠ Помилка мережі", Toast.LENGTH_SHORT).show()
                    );
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "❌ Помилка обробки зображення!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
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

    private void uploadExercise(String title, String videoUrl, String durationSec, String previewImageUrl, String workoutId) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        String json = "{" +
                "\"title\":\"" + title + "\"," +
                "\"videoUrl\":\"" + videoUrl + "\"," +
                "\"durationSeconds\":\"" + durationSec + "\"," +
                "\"previewImageUrl\":\"" + previewImageUrl + "\"," +
                "\"workoutId\":\"" + workoutId + "\"" +
                "}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(ApiClient.BASE_URL + "/api/workouts/addExercise") // ✅ Використовуємо POST для створення нового уроку
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "✅ Урок додано!", Toast.LENGTH_SHORT).show();
                        fetchExercises(); // ✅ Оновлення списку після додавання
                    } else {
                        Toast.makeText(getContext(), "❌ Помилка при додаванні уроку!", Toast.LENGTH_SHORT).show();
                        Log.e("uploadExercise", "Код: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "⚠ Помилка мережі!", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }


    private void updateExercise(String exerciseId, String title, String videoUrl, String durationSec, String previewImageUrl) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        String json = "{" +
                "\"exerciseId\":\"" + exerciseId + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"videoUrl\":\"" + videoUrl + "\"," +
                "\"durationSeconds\":\"" + durationSec + "\"," +
                "\"previewImageUrl\":\"" + previewImageUrl + "\"" +
                "}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(ApiClient.BASE_URL + "/api/workouts/updateExercise") // ✅ Використовуємо PUT
                .put(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "✅ Урок оновлено!", Toast.LENGTH_SHORT).show();
                        fetchExercises(); // ✅ Оновлення списку після змін
                    } else {
                        Toast.makeText(getContext(), "❌ Помилка редагування!", Toast.LENGTH_SHORT).show();
                        Log.e("updateExercise", "Код: " + response.code());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "⚠ Помилка мережі!", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }



    private void fetchExercises() {
        String url = ApiClient.BASE_URL + "/api/workouts/" + workoutId + "/exercises";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Exercise>>() {}.getType();
                    List<Exercise> loadedExercises = gson.fromJson(json, listType);

                    requireActivity().runOnUiThread(() -> {
                        exerciseList.clear();
                        exerciseList.addAll(loadedExercises);
                        exerciseAdapter.notifyDataSetChanged();
                    });
                } else {
                    Log.e("fetchExercises", "❌ Не вдалося завантажити вправи");
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("fetchExercises", "❌ Запит не вдався: " + e.getMessage());
            }
        });
    }
}
