package com.dev.fellpulse_hub;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class activityDetectorEmociones extends AppCompatActivity {

    private CalendarView emotionCalendarView;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;
    private TextView tip1, tip2, tip3, tip4, tip5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detector_emociones);

        inicializarVistas();
        configurarNavegacion();
        cargarTipsDeEjemplo();

        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);
    }

    private void inicializarVistas() {
        emotionCalendarView = findViewById(R.id.emotionCalendarView);
        tip1 = findViewById(R.id.tip1);
        tip2 = findViewById(R.id.tip2);
        tip3 = findViewById(R.id.tip3);
        tip4 = findViewById(R.id.tip4);
        tip5 = findViewById(R.id.tip5);
        
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);
    }

    private void cargarTipsDeEjemplo() {
        tip1.setText("1. Sal a caminar 10 minutos para despejar tu mente.");
        tip2.setText("2. Escribe tres cosas por las que te sientas agradecido hoy.");
        tip3.setText("3. Escucha tu canción favorita para levantar el ánimo.");
        tip4.setText("4. Habla con un amigo o familiar sobre cómo te sientes.");
        tip5.setText("5. Dedica 5 minutos a una respiración profunda y consciente.");
    }

    private void configurarNavegacion() {
        btnHeart.setOnClickListener(v -> {
            Intent intent = new Intent(activityDetectorEmociones.this, activityInicio.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // Como "Detector" no está en la barra, no se selecciona ningún ítem.
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, activityCuenta.class));
            } else if (id == R.id.nav_creators) {
                startActivity(new Intent(this, activityCreadores.class));
            } else if (id == R.id.nav_statistics) {
                startActivity(new Intent(this, activityEstadisticas.class));
            } else if (id == R.id.nav_daily_aura) {
                startActivity(new Intent(this, activityDiario.class));
            }
            return true;
        });
    }
}
