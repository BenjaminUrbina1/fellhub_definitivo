package com.dev.fellpulse_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activityCreadores extends AppCompatActivity {

    private TextInputEditText etContactMessage;
    private Button btnSendMessage;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creadores);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
                enviarMensajeAFirebase(message);
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

    private void enviarMensajeAFirebase(String mensaje) {
        btnSendMessage.setEnabled(false); // Evitar doble clic

        // Obtener UID del usuario
        FirebaseUser user = mAuth.getCurrentUser();
        String uid;
        if (user != null) {
            uid = user.getUid();
        } else {
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            uid = prefs.getString("user_uid", "anonimo");
        }

        // Crear el objeto de datos
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("usuario_id", uid);
        feedback.put("mensaje", mensaje);
        feedback.put("tipo", "Contacto Creadores"); // Etiqueta para identificarlo
        feedback.put("fecha", new Date());
        feedback.put("estado", "Pendiente"); // Para que tú sepas si ya lo leíste

        // Guardar en la colección "retroalimentacion"
        db.collection("retroalimentacion")
                .add(feedback)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Mensaje enviado! Gracias por contactarnos.", Toast.LENGTH_LONG).show();
                    etContactMessage.setText("");
                    btnSendMessage.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al enviar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSendMessage.setEnabled(true);
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
