
package com.example.project1.trenning;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import android.widget.Toast;

import com.example.project1.ApiClient;
import com.example.project1.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_training extends Fragment {
    private RecyclerView recyclerView;
    private WorkoutAdapter adapter;
    private List<Workout> workouts = new ArrayList<>();
    private FetchWorkouts fetchWorkouts;
    private Button button_add;
    private static final int IMAGE_PICK_CODE = 1000;
    private final OkHttpClient client = new OkHttpClient();
    private String selectedImagePath = "";
    private String uploadedImageName = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        recyclerView = view.findViewById(R.id.list_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkoutAdapter(workouts);
        recyclerView.setAdapter(adapter);
        button_add = view.findViewById(R.id.button_add);

        button_add.setOnClickListener(v -> showWorkoutDialog());

        adapter.setOnCategoryClickListener(workout -> {
            if (isAdded()) {
                Fragment_training_lessons fragment = new Fragment_training_lessons();
                Bundle bundle = new Bundle();
                bundle.putString("workoutId", workout.getWorkoutId());
                bundle.putString("title", workout.getTitle());
                bundle.putString("duration", workout.getDurationAll());
                bundle.putString("exercise", workout.getExercise());
                bundle.putString("imagePath", workout.getPicPath());
                fragment.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void showWorkoutDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_workout, null);
        bottomSheetDialog.setContentView(view);

        EditText editTextTitle = view.findViewById(R.id.editTextWorkoutTitle);
        EditText editTextDescription = view.findViewById(R.id.editTextWorkoutDescription);
        EditText editTextDuration = view.findViewById(R.id.editTextWorkoutDuration);
        EditText editTextExercise = view.findViewById(R.id.editTextWorkoutExercise);
        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        Button buttonSaveWorkout = view.findViewById(R.id.buttonSaveWorkout);

        buttonSelectImage.setOnClickListener(v -> selectImage());

        buttonSaveWorkout.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            String duration = editTextDuration.getText().toString().trim();
            String exercise = editTextExercise.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || duration.isEmpty() || exercise.isEmpty()) {
                Toast.makeText(getContext(), "Заповніть всі поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uploadedImageName != null) {
                uploadWorkout(title, description, duration, exercise, uploadedImageName);
            } else {
                Toast.makeText(getContext(), "Зачекай, поки зображення завантажиться!", Toast.LENGTH_SHORT).show();
            }

            bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.show();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                selectedImagePath = imageUri.toString(); // Збережи URI для подальшого використання

                uploadImage(imageUri, fileNameWithoutExtension -> {
                    // Коли зображення завантажено — збережи його назву
                    uploadedImageName = fileNameWithoutExtension;
                });
            }
        }
    }


    private void uploadImage(Uri imageUri, Consumer<String> onSuccess) {
        try {
            ContentResolver contentResolver = requireContext().getContentResolver();

            String fileName = getFileName(imageUri);
            if (fileName == null) {
                Toast.makeText(getContext(), "❌ Не вдалося отримати ім’я файлу!", Toast.LENGTH_SHORT).show();
                return;
            }

            String nameWithoutExtension = fileName.contains(".")
                    ? fileName.substring(0, fileName.lastIndexOf('.'))
                    : fileName;

            InputStream inputStream = contentResolver.openInputStream(imageUri);
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
                    .addFormDataPart(
                            "file",
                            nameWithoutExtension + ".png",
                            RequestBody.create(pngBytes, MediaType.parse("image/png"))
                    )
                    .build();

            Request request = new Request.Builder()
                    .url(ApiClient.BASE_URL + "/api/workouts/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    requireActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "✅ Зображення завантажено!", Toast.LENGTH_SHORT).show();
                            Log.d("UPLOAD_SUCCESS", "File uploaded: " + nameWithoutExtension);
                            onSuccess.accept(nameWithoutExtension);
                        } else {
                            Toast.makeText(getContext(), "❌ Помилка завантаження!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "⚠️ Помилка мережі!", Toast.LENGTH_SHORT).show());
                    Log.e("UPLOAD_ERROR", "Exception: ", e);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "❌ Помилка обробки зображення!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver()
                    .query(uri, null, null, null, null)) {
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



    private void uploadWorkout(String title, String description, String duration, String exercise, String imagePath) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{\"title\":\"" + title + "\",\"description\":\"" + description + "\",\"durationAll\":\"" + duration + "\",\"exercise\":\"" + exercise + "\",\"picPath\":\"" + imagePath + "\"}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(ApiClient.BASE_URL + "/api/workouts/add")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "✅ Тренування додане!", Toast.LENGTH_SHORT).show();
                        loadWorkouts();
                    } else {
                        Toast.makeText(getContext(), "❌ Помилка збереження!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "⚠️ Помилка підключення!", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadWorkouts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fetchWorkouts != null) {
            fetchWorkouts.cancel();
        }
    }

    private void loadWorkouts() {
        if (!isAdded() || getContext() == null) return;

        fetchWorkouts = new FetchWorkouts(getContext()) {
            @Override
            protected void onDataLoaded(List<Workout> data) {
                if (isAdded() && adapter != null) {
                    workouts.clear();
                    workouts.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onError(String errorMessage) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        };
        fetchWorkouts.fetchWorkouts();
    }
}