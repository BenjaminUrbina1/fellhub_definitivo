package com.dev.fellpulse_hub;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class activityHabitosSaludables extends AppCompatActivity {

    private EditText etHabito;
    private Button btnEnviarHabito;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitos_saludables);

        etHabito = findViewById(R.id.etHabito);
        btnEnviarHabito = findViewById(R.id.btnEnviarHabito);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);

        btnEnviarHabito.setOnClickListener(v -> {
            String habito = etHabito.getText().toString().trim();
            if (!habito.isEmpty()) {
                Toast.makeText(this, "¡Gracias por compartir tu hábito!", Toast.LENGTH_SHORT).show();
                etHabito.setText("");
            } else {
                Toast.makeText(this, "Por favor, escribe un hábito", Toast.LENGTH_SHORT).show();
            }
        });

        configurarNavegacion();

        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);
    }

    private void configurarNavegacion() {
        btnHeart.setOnClickListener(v -> {
            Intent intent = new Intent(activityHabitosSaludables.this, activityInicio.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

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
