package com.dev.fellpulse_hub;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HistorialEmocionesManager {

    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;

    public HistorialEmocionesManager() {
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    // Interfaz para saber si se guardó bien o mal (para el otro desarrollador)
    public interface GuardarCallback {
        void onExito();
        void onError(String error);
    }

    /**
     * Guarda un registro en la colección 'historial_emociones'.
     * Estructura diseñada para privacidad y análisis médico futuro.
     *
     * @param bpm         Valor del sensor de pulso.
     * @param spo2        Valor de saturación de oxígeno (opcional, enviar 0 si no hay).
     * @param nombreEmocion Nombre de la emoción (ej: "Ira", "Felicidad").
     * @param colorHex    Código de color para la UI (ej: "#FF0000").
     * @param callback    Respuesta asíncrona.
     */
    public void guardarLectura(int bpm, int spo2, String nombreEmocion, String colorHex, GuardarCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            callback.onError("Usuario no autenticado. No se puede guardar el historial.");
            return;
        }

        // 1. Estructura de Datos Biométrica (Datos crudos)
        Map<String, Object> biometria = new HashMap<>();
        biometria.put("bpm", bpm);
        biometria.put("spo2", spo2);

        // 2. Estructura de Resultado (Interpretación)
        Map<String, Object> emocionData = new HashMap<>();
        emocionData.put("nombre", nombreEmocion);
        emocionData.put("color_hex", colorHex);

        // 3. Documento Maestro
        Map<String, Object> historialEntry = new HashMap<>();
        historialEntry.put("usuario_id", user.getUid()); // CLAVE para la privacidad
        historialEntry.put("fecha", new Date());         // Timestamp del servidor
        historialEntry.put("biometria", biometria);
        historialEntry.put("resultado", emocionData);

        // Guardar en Firestore
        db.collection("historial_emociones")
                .add(historialEntry)
                .addOnSuccessListener(documentReference -> callback.onExito())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Método utilitario para que el otro desarrollador recupere SOLO el historial
     * del usuario actual.
     */
    public Query obtenerHistorialUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return null;

        return db.collection("historial_emociones")
                .whereEqualTo("usuario_id", user.getUid()) // Filtro de privacidad a nivel de consulta
                .orderBy("fecha", Query.Direction.DESCENDING);
    }
}
