package com.dev.fellpulse_hub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activityDetectorEmociones extends AppCompatActivity {

    private CalendarView emotionCalendarView;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;
    
    private Button btnDetectar;
    private TextView tvEstadoSensor;
    private LinearLayout containerTips;
    private TextView tip1, tip2, tip3, tip4, tip5;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detector_emociones);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        inicializarVistas();
        configurarNavegacion();
        
        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);

        /*
         * =========================================================================================
         *  ZONA DE INTEGRACIÓN ARDUINO
         * =========================================================================================
         */
        btnDetectar.setOnClickListener(v -> {
            tvEstadoSensor.setText("Conectando con sensor... Pon tu dedo.");
            containerTips.setVisibility(View.GONE); 
            
            // SIMULACIÓN
            v.postDelayed(() -> {
                int bpmLeido = 115; 
                int spo2Leido = 98;
                String emocionCalculada = "Ira"; 
                String colorHex = "#FF0000";

                procesarResultadoArduino(bpmLeido, spo2Leido, emocionCalculada, colorHex);
                
            }, 2000);
        });
    }

    private void inicializarVistas() {
        emotionCalendarView = findViewById(R.id.emotionCalendarView);
        
        btnDetectar = findViewById(R.id.btnDetectar);
        tvEstadoSensor = findViewById(R.id.tvEstadoSensor);
        containerTips = findViewById(R.id.containerTips); 
        
        tip1 = findViewById(R.id.tip1);
        tip2 = findViewById(R.id.tip2);
        tip3 = findViewById(R.id.tip3);
        tip4 = findViewById(R.id.tip4);
        tip5 = findViewById(R.id.tip5);
        
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);
    }

    private void procesarResultadoArduino(int bpm, int spo2, String nombreEmocion, String colorHex) {
        tvEstadoSensor.setText("Detección finalizada: " + nombreEmocion + " (" + bpm + " BPM)");
        
        // Cargar Tips desde Firebase
        cargarTipsDesdeFirebase(nombreEmocion);

        guardarRegistroArduino(bpm, spo2, nombreEmocion, colorHex);
    }

    private void cargarTipsDesdeFirebase(String emocion) {
        String docId = emocion.toLowerCase();

        db.collection("configuracion_rueda").document(docId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("tips")) {
                        List<String> tipsDb = (List<String>) documentSnapshot.get("tips");
                        mostrarTipsEnPantalla(tipsDb);
                    } else {
                        cargarTipsPredeterminados(emocion);
                    }
                })
                .addOnFailureListener(e -> cargarTipsPredeterminados(emocion));
    }

    private void mostrarTipsEnPantalla(List<String> tips) {
        if (tips == null || tips.isEmpty()) return;

        if (tips.size() > 0) tip1.setText("1. " + tips.get(0));
        if (tips.size() > 1) tip2.setText("2. " + tips.get(1));
        if (tips.size() > 2) tip3.setText("3. " + tips.get(2));
        if (tips.size() > 3) tip4.setText("4. " + tips.get(3));
        if (tips.size() > 4) tip5.setText("5. " + tips.get(4));

        containerTips.setVisibility(View.VISIBLE);
        containerTips.setAlpha(0f);
        containerTips.animate().alpha(1f).setDuration(500);
    }

    private void cargarTipsPredeterminados(String emocion) {
        String[] tips;
        switch (emocion) {
            case "Ira":
                tips = new String[]{"Respira profundo.", "Aléjate.", "Haz ejercicio.", "Escribe.", "Escucha música."}; break;
            case "Tristeza":
                tips = new String[]{"Llora si quieres.", "Habla con alguien.", "Pasea.", "Haz algo que te guste.", "Pasará."}; break;
            case "Felicidad":
                tips = new String[]{"Disfruta.", "Anota.", "Avanza proyectos.", "Sonríe.", "Comparte."}; break;
            case "Sorpresa":
                tips = new String[]{"Procesa.", "Celebra.", "Aprende.", "Mente abierta.", "Observa."}; break;
            case "Miedo":
                tips = new String[]{"Identifica.", "Respira 4x4.", "Recuerda logros.", "Busca seguridad.", "Visualiza."}; break;
            case "Disgusto":
                tips = new String[]{"Aléjate.", "Lávate la cara.", "Enfócate en algo bello.", "Expresa.", "Acepta."}; break;
            default:
                tips = new String[]{"Respira.", "Bebe agua.", "Observa.", "Estira.", "Conecta."}; break;
        }
        java.util.ArrayList<String> listaTips = new java.util.ArrayList<>();
        for(String t : tips) listaTips.add(t);
        mostrarTipsEnPantalla(listaTips);
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

    private void guardarRegistroArduino(int bpm, int spo2, String nombreEmocion, String colorHex) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Usuario no identificado.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> biometria = new HashMap<>();
        biometria.put("bpm", bpm);
        biometria.put("spo2", spo2);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("nombre", nombreEmocion);
        resultado.put("color_hex", colorHex);

        Map<String, Object> registro = new HashMap<>();
        // registro.put("usuario_id", user.getUid()); // Ya no es necesario guardar ID dentro del doc si está en la ruta
        registro.put("fecha", new Date());
        registro.put("biometria", biometria);
        registro.put("resultado", resultado);

        // RUTA NUEVA: users/{uid}/historial_emociones
        db.collection("users").document(user.getUid()).collection("historial_emociones")
                .add(registro)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "¡Lectura guardada!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar historial.", Toast.LENGTH_SHORT).show();
                });
    }
}
