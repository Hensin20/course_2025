package com.example.project1;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.project1.trenning.Fragment_training;
import com.example.project1.calendar.Fragment_calendar;

public class activity_main_1 extends AppCompatActivity {

    private ImageButton img_btn_calendar, img_btn_trening, img_btn_user;
    Fragment_calendar fragment_calendar = new Fragment_calendar();
    Fragment_profile fragment_profile = new Fragment_profile();
    Fragment_training fragment_training = new Fragment_training();

    @SuppressLint("MissingInflatedId")
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

        img_btn_calendar = findViewById(R.id.imageButton_calendar);
        img_btn_trening = findViewById(R.id.imageButton_trening);
        img_btn_user = findViewById(R.id.imageButton_user);

        SharedPreferences prefs = getSharedPreferences("userPrefs", MODE_PRIVATE);

        String userRole = prefs.getString("userRole", "user"); // За замовчуванням "user"

        //ImageButton adminButton = findViewById(R.id.imageButton_admin);
       // Log.d("main", "userRole: " + userRole);
       // if ("admin".equals(userRole)) {
       //     adminButton.setVisibility(View.VISIBLE); // Показуємо кнопку адміна
       // } else {
       //     adminButton.setVisibility(View.GONE); // Приховуємо для звичайних користувачів
       // }

        setNewFragment(fragment_calendar);

        img_btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewFragment(fragment_calendar);
            }

        });

        img_btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { setNewFragment(fragment_profile); }
        });

        img_btn_trening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {setNewFragment(fragment_training);}
        });
    }



    public void setNewFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout,fragment);
        ft.commit();
    }

}