package com.dev.fellpulse_hub;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Imports de Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activityDetectorEmociones extends AppCompatActivity {

    private CalendarView emotionCalendarView;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;
    private TextView tip1, tip2, tip3, tip4, tip5;

    // Variables para Base de Datos
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detector_emociones);

        // 1. Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        inicializarVistas();
        configurarNavegacion();
        cargarTipsDeEjemplo();

        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);

        // -----------------------------------------------------------------------
        // ZONA PARA EL DESARROLLADOR DEL ARDUINO:
        // Aquí deberías configurar el Listener de tu botón "Escanear" o "Conectar".
        //
        // Ejemplo:
        // btnEscanear.setOnClickListener(v -> {
        //      // 1. Ejecutar lógica de lectura del sensor...
        //      // 2. Al tener los resultados (bpm, spo2, emoción), llamar a:
        //      guardarRegistroArduino(115, 98, "Ira", "#FF0000");
        // });
        // -----------------------------------------------------------------------
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

    /**
     * =============================================================================
     * MÉTODO DE GUARDADO EN BASE DE DATOS (Usar este método)
     * =============================================================================
     * Este método recibe los datos del sensor y los sube a Firebase.
     *
     * @param bpm         Ritmo cardíaco leído (int).
     * @param spo2        Oxígeno en sangre leído (int).
     * @param nombreEmocion Nombre de la emoción calculada (String).
     * @param colorHex    Color hexadecimal asociado (String).
     */
    private void guardarRegistroArduino(int bpm, int spo2, String nombreEmocion, String colorHex) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Error: Usuario no identificado. Inicia sesión.", Toast.LENGTH_LONG).show();
            return;
        }

        // Preparar estructuras de datos
        Map<String, Object> biometria = new HashMap<>();
        biometria.put("bpm", bpm);
        biometria.put("spo2", spo2);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("nombre", nombreEmocion);
        resultado.put("color_hex", colorHex);

        Map<String, Object> registro = new HashMap<>();
        registro.put("usuario_id", user.getUid());
        registro.put("fecha", new Date());
        registro.put("biometria", biometria);
        registro.put("resultado", resultado);

        // Enviar a la colección "historial_emociones"
        db.collection("historial_emociones")
                .add(registro)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "¡Lectura guardada! Emoción: " + nombreEmocion, Toast.LENGTH_SHORT).show();
                    // Aquí podrías agregar lógica para actualizar la UI, pintar el fondo, etc.
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
