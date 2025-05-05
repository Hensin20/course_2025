package com.example.project1;

import android.os.Bundle;
import android.text.TextUtils;
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
        String json = "{\"phoneNumber\":\"" + phone + "\",\"password\":\"" + password + "\"}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/auth/register")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(Activity_registration.this, "Реєстрація успішна!", Toast.LENGTH_SHORT).show();
                        finish(); // повернення назад
                    } else {
                        Toast.makeText(Activity_registration.this, "Помилка: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Activity_registration.this, "Помилка підключення", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
