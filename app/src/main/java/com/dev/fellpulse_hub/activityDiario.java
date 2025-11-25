package com.dev.fellpulse_hub;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class activityDiario extends AppCompatActivity {

    // Vistas para el nuevo diseño del diario
    private ImageView ivEmotionWheel;
    private Button btnWriteMorning;
    private Button btnWriteNight;

    // Vistas comunes
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario);

        inicializarVistas();
        configurarListeners();
        configurarBottomNavigation();

        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);
    }

    private void inicializarVistas() {
        // Vistas del diario
        ivEmotionWheel = findViewById(R.id.ivEmotionWheel);
        btnWriteMorning = findViewById(R.id.btnWriteMorning);
        btnWriteNight = findViewById(R.id.btnWriteNight);
        
        // Vistas comunes
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);
    }

    private void configurarListeners() {
        // Lógica para la rueda de emociones (futura implementación)
        ivEmotionWheel.setOnClickListener(v -> {
            Toast.makeText(this, "Rueda de emociones pulsada", Toast.LENGTH_SHORT).show();
        });
        
        // Listener para el botón "Escribir" Matutino
        btnWriteMorning.setOnClickListener(v -> {
            Intent intent = new Intent(activityDiario.this, activityEscribirDiario.class);
            startActivity(intent);
        });

        // Listener para el botón "Escribir" Nocturno
        btnWriteNight.setOnClickListener(v -> {
            Intent intent = new Intent(activityDiario.this, activityEscribirDiario.class);
            startActivity(intent);
        });

        btnHeart.setOnClickListener(v -> {
            Intent intent = new Intent(activityDiario.this, activityInicio.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void configurarBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_daily_aura);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, activityCuenta.class));
            } else if (id == R.id.nav_creators) {
                startActivity(new Intent(this, activityCreadores.class));
            } else if (id == R.id.nav_statistics) {
                startActivity(new Intent(this, activityEstadisticas.class));
            } else if (id == R.id.nav_daily_aura) {
                return true;
            }
            return true;
        });
    }
}
