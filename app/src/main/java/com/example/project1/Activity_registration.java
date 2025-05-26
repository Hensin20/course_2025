package com.example.project1;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.*;

public class Activity_registration extends AppCompatActivity {

    private EditText editTextPhone, editTextPassword;
    private Button buttonRegister;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextPhone = findViewById(R.id.editTextPhone_registration);
        editTextPassword = findViewById(R.id.editTextTextPassword_registration);
        buttonRegister = findViewById(R.id.button_registration2);

        buttonRegister.setOnClickListener(v -> {
            String phone = editTextPhone.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(phone)) {
                editTextPhone.setError("Введіть номер телефону");
                return;
            }

            if (TextUtils.isEmpty(password) || password.length() < 6) {
                editTextPassword.setError("Пароль має містити мінімум 6 символів");
                return;
            }

            registerUser(phone, password);
        });
    }

    private void registerUser(String phone, String password) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{\"phoneNumber\":\"" + phone + "\",\"password\":\"" + password + "\",\"roleAdmin\":\"user\"}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(ApiClient.BASE_URL +"/api/auth/register")
                .post(body)
                .addHeader("Content-Type", "application/json") // Додаємо заголовок
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body() != null ? response.body().string() : "Empty response";

                runOnUiThread(() -> {
                    Log.d("Register", "Response code: " + response.code());
                    Log.d("Register", "Response body: " + responseText);

                    if (response.isSuccessful()) {
                        Toast.makeText(Activity_registration.this, "✅ Реєстрація успішна!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(Activity_registration.this, "❌ Помилка: " + responseText, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Log.e("Register", "Помилка запиту: " + e.getMessage());
                    Toast.makeText(Activity_registration.this, "⚠ Помилка підключення", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
