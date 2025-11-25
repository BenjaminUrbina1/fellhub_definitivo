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

public class activityHabitosNoSaludables extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;
    private EditText etHabitoNoSaludable;
    private Button btnEnviarHabitoNoSaludable;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitos_no_saludables);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);
        etHabitoNoSaludable = findViewById(R.id.etHabitoNoSaludable);
        btnEnviarHabitoNoSaludable = findViewById(R.id.btnEnviarHabitoNoSaludable);

        configurarNavegacion();
        configurarBotonEnviar();

        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);
    }

    private void configurarNavegacion() {
        btnHeart.setOnClickListener(v -> {
            Intent intent = new Intent(activityHabitosNoSaludables.this, activityInicio.class);
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

    private void configurarBotonEnviar() {
        btnEnviarHabitoNoSaludable.setOnClickListener(v -> {
            String habito = etHabitoNoSaludable.getText().toString().trim();
            if (!habito.isEmpty()) {
                guardarHabitoNoSaludable(habito);
            } else {
                Toast.makeText(this, "Por favor, escribe un hábito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarHabitoNoSaludable(String texto) {
        btnEnviarHabitoNoSaludable.setEnabled(false); // Evitar doble clic

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
        Map<String, Object> data = new HashMap<>();
        data.put("usuario_id", uid);
        data.put("descripcion", texto);
        data.put("tipo", "no_saludable"); // Diferenciador clave
        data.put("fecha", new Date());

        // Guardar en Firestore
        db.collection("habitos")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Gracias por compartir! Reconocerlo es el primer paso.", Toast.LENGTH_SHORT).show();
                    etHabitoNoSaludable.setText("");
                    btnEnviarHabitoNoSaludable.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnEnviarHabitoNoSaludable.setEnabled(true);
                });
    }
}
