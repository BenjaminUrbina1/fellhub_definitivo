package com.dev.fellpulse_hub;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class activityCuenta extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;
    
    // Vistas del formulario de edición
    private TextInputEditText etUsername, etNickname, etEmail, etPhone;
    private Button btnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);

        // Inicializar vistas
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);
        etUsername = findViewById(R.id.etUsername);
        etNickname = findViewById(R.id.etNickname);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        configurarListeners();
        configurarBottomNavigation();

        // Iniciar la animación del corazón
        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);
    }

    private void configurarListeners() {
        btnSaveChanges.setOnClickListener(v -> {
            Toast.makeText(this, "Cambios guardados (simulación)", Toast.LENGTH_SHORT).show();
        });

        btnHeart.setOnClickListener(v -> {
            Intent intent = new Intent(activityCuenta.this, activityInicio.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void configurarBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_user_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_profile) {
                return true; // Ya estamos aquí
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
