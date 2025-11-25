package com.dev.fellpulse_hub;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class InicializadorDatos {

    public static void cargarConfiguracionInicial() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. TRISTEZA (Verde) - Ritmo bajo
        Map<String, Object> tristeza = new HashMap<>();
        tristeza.put("nombre", "Tristeza");
        tristeza.put("color_hex", "#4CAF50");
        tristeza.put("descripcion", "Ritmo cardíaco bajo. Estado de baja energía o melancolía.");
        tristeza.put("bpm_min", 50);
        tristeza.put("bpm_max", 69);
        db.collection("configuracion_rueda").document("tristeza").set(tristeza);

        // 2. FELICIDAD (Mostaza) - Ritmo normal
        Map<String, Object> felicidad = new HashMap<>();
        felicidad.put("nombre", "Felicidad");
        felicidad.put("color_hex", "#FFC107");
        felicidad.put("descripcion", "Ritmo estable y moderado. Estado de bienestar.");
        felicidad.put("bpm_min", 70);
        felicidad.put("bpm_max", 85);
        db.collection("configuracion_rueda").document("felicidad").set(felicidad);

        // 3. SORPRESA (Azul) - Leve aceleración
        Map<String, Object> sorpresa = new HashMap<>();
        sorpresa.put("nombre", "Sorpresa");
        sorpresa.put("color_hex", "#2196F3");
        sorpresa.put("descripcion", "Incremento repentino en el ritmo.");
        sorpresa.put("bpm_min", 86);
        sorpresa.put("bpm_max", 97);
        db.collection("configuracion_rueda").document("sorpresa").set(sorpresa);

        // 4. DISGUSTO (Café) - Acelerado molesto
        Map<String, Object> disgusto = new HashMap<>();
        disgusto.put("nombre", "Disgusto");
        disgusto.put("color_hex", "#795548");
        disgusto.put("descripcion", "Reacción de rechazo con pulso elevado.");
        disgusto.put("bpm_min", 98);
        disgusto.put("bpm_max", 109);
        db.collection("configuracion_rueda").document("disgusto").set(disgusto);

        // 5. IRA (Rojo) - Muy acelerado
        Map<String, Object> ira = new HashMap<>();
        ira.put("nombre", "Ira");
        ira.put("color_hex", "#FF0000");
        ira.put("descripcion", "Ritmo cardíaco muy elevado. Estado de alerta o enfado.");
        ira.put("bpm_min", 110);
        ira.put("bpm_max", 135);
        db.collection("configuracion_rueda").document("ira").set(ira);

        // 6. MIEDO (Morado) - Pánico extremo
        Map<String, Object> miedo = new HashMap<>();
        miedo.put("nombre", "Miedo");
        miedo.put("color_hex", "#9C27B0");
        miedo.put("descripcion", "Ritmo cardíaco extremo e irregular (Pánico).");
        miedo.put("bpm_min", 136);
        miedo.put("bpm_max", 180); // Subimos el máximo para cubrir picos altos
        db.collection("configuracion_rueda").document("miedo").set(miedo);

        Log.d("FellPulse", "Configuración de emociones (Rueda) enviada a Firebase.");
    }
}
