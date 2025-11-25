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

public class activityCreadores extends AppCompatActivity {

    private TextInputEditText etContactMessage;
    private Button btnSendMessage;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creadores);

        // Inicializar vistas
        etContactMessage = findViewById(R.id.etContactMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);

        configurarListeners();
        configurarBottomNavigation();

        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);
    }

    private void configurarListeners() {
        btnSendMessage.setOnClickListener(v -> {
            String message = etContactMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                Toast.makeText(this, "¡Gracias por tu mensaje! Lo revisaremos pronto.", Toast.LENGTH_LONG).show();
                etContactMessage.setText("");
            } else {
                Toast.makeText(this, "Por favor, escribe un mensaje antes de enviar.", Toast.LENGTH_SHORT).show();
            }
        });

        btnHeart.setOnClickListener(v -> {
            Intent intent = new Intent(activityCreadores.this, activityInicio.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private void configurarBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_creators);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, activityCuenta.class));
            } else if (id == R.id.nav_creators) {
                return true; // Ya estamos aquí
            } else if (id == R.id.nav_statistics) {
                startActivity(new Intent(this, activityEstadisticas.class));
            } else if (id == R.id.nav_daily_aura) {
                startActivity(new Intent(this, activityDiario.class));
            }
            return true;
        });
    }
}
