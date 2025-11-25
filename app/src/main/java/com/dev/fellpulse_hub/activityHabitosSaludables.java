package com.dev.fellpulse_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activityHabitosSaludables extends AppCompatActivity {

    private EditText etHabito;
    private Button btnEnviarHabito;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitos_saludables);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etHabito = findViewById(R.id.etHabito);
        btnEnviarHabito = findViewById(R.id.btnEnviarHabito);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);

        btnEnviarHabito.setOnClickListener(v -> {
            String habito = etHabito.getText().toString().trim();
            if (!habito.isEmpty()) {
                guardarHabito(habito);
            } else {
                Toast.makeText(this, "Por favor, escribe un hábito", Toast.LENGTH_SHORT).show();
            }
        });

        configurarNavegacion();

        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);
    }

    private void guardarHabito(String textoHabito) {
        btnEnviarHabito.setEnabled(false); // Evitar doble click

        // Obtener UID
        FirebaseUser user = mAuth.getCurrentUser();
        String uid;
        if (user != null) {
            uid = user.getUid();
        } else {
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            uid = prefs.getString("user_uid", "anonimo");
        }

        // Crear mapa de datos
        Map<String, Object> habito = new HashMap<>();
        habito.put("usuario_id", uid);
        habito.put("descripcion", textoHabito);
        habito.put("tipo", "saludable");
        habito.put("fecha", new Date());

        // Guardar en Firestore
        db.collection("habitos")
                .add(habito)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Hábito saludable guardado!", Toast.LENGTH_SHORT).show();
                    etHabito.setText("");
                    btnEnviarHabito.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnEnviarHabito.setEnabled(true);
                });
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
