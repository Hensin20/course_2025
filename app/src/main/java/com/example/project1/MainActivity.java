package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText editTextPhone_login, editTextTextPassword_login;
    private TextView textView_hint;
    private Button button_login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
        editTextPhone_login = findViewById(R.id.editTextPhone_login);
        editTextTextPassword_login = findViewById(R.id.editTextTextPassword_login);
        button_login = findViewById(R.id.button_login);

        editTextPhone_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            String phone = s.toString();
            if(isValidPhoneNumber(phone)){
                editTextPhone_login.setError(null);
            }
            else{
                editTextPhone_login.setError("Невірний формат номера");
            }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
            public boolean isValidPhoneNumber(String phone){
                String phonePattern = "^(\\+?38)?0\\d{9}$";
                return phone.matches(phonePattern);
            }
        });

        editTextTextPassword_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if(isValidPassword(password)){
                    editTextTextPassword_login.setError(null);
                }
                else{
                    editTextTextPassword_login.setError("Пароль має містити: мінімум 6 символів");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
            public boolean isValidPassword(String password){
                String passwordPattern = ".{6,}";
                return password.matches(passwordPattern);
            }
        });


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editTextPhone_login.getText().toString();
                String password = editTextTextPassword_login.getText().toString();
                if(TextUtils.isEmpty((phone))){
                    editTextPhone_login.setError("Введіть номер телефону");
                }
                if(TextUtils.isEmpty(password)){
                    editTextTextPassword_login.setError("Введіть пароль");
                }
                Intent intent = new Intent(MainActivity.this, activity_main_1.class);
                startActivity(intent);
            }
        });

    }
    public void button_registration (View v){
        Intent intent = new Intent(this,Activity_registration.class);
        startActivity(intent);
    }
}