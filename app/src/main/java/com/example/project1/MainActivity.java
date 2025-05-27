package com.example.project1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText editTextPhone_login, editTextTextPassword_login;
    private Button button_login;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextPhone_login = findViewById(R.id.editTextPhone_login);
        editTextTextPassword_login = findViewById(R.id.editTextTextPassword_login);
        button_login = findViewById(R.id.button_login);

        // Перевірка номера
        editTextPhone_login.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidPhoneNumber(s.toString())) {
                    editTextPhone_login.setError(null);
                } else {
                    editTextPhone_login.setError("Невірний формат номера");
                }
            }

            private boolean isValidPhoneNumber(String phone) {
                String phonePattern = "^(\\+?38)?0\\d{9}$";
                return phone.matches(phonePattern);
            }
        });

        // Перевірка пароля
        editTextTextPassword_login.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    editTextTextPassword_login.setError(null);
                } else {
                    editTextTextPassword_login.setError("Пароль має містити мінімум 6 символів");
                }
            }
        });

        // Кнопка входу
        button_login.setOnClickListener(v -> {
            String phone = editTextPhone_login.getText().toString();
            String password = editTextTextPassword_login.getText().toString();

            if (TextUtils.isEmpty(phone)) {
                editTextPhone_login.setError("Введіть номер телефону");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextTextPassword_login.setError("Введіть пароль");
                return;
            }

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            String json = "{\"phoneNumber\":\"" + phone + "\",\"password\":\"" + password + "\",\"roleAdmin\":\"user\"}";


            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(ApiClient.BASE_URL +"/api/auth/login")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "{}";

                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(responseBody);
                            String token = json.getString("token");
                            String role = json.optString("roleAdmin", "user"); // Виправлено на roleAdmin
                            String userId = json.getString("userId");
                            // Отримуємо роль

                            // Зберігаємо дані користувача
                            SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("jwt_token", token)
                                    .putString("phoneNumber", phone)
                                    .putString("userId", userId)
                                    .putString("userRole", role) // Зберігаємо роль
                                    .apply();

                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "✅ Вхід успішний!", Toast.LENGTH_SHORT).show();
                                Log.d("main", "userRole: " + role);
                                Intent intent = new Intent(MainActivity.this, activity_main_1.class);
                                startActivity(intent);
                                finish(); // Закриваємо поточну активність
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, "❌ Помилка обробки відповіді", Toast.LENGTH_SHORT).show()
                            );
                        }
                    } else {
                        Log.d("Login", "Server response: " + responseBody);
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "❌ Невірний логін або пароль", Toast.LENGTH_SHORT).show()
                        );
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "⚠ Помилка підключення: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });
    }

    // Кнопка для переходу на екран реєстрації
    public void button_registration(View v) {
        Intent intent = new Intent(this, Activity_registration.class);
        startActivity(intent);
    }
}
