package com.dev.fellpulse_hub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activityEscribirDiario extends AppCompatActivity {

    private AutoCompleteTextView actvPrimaryEmotion;
    private TextInputEditText etEmotionCause, etFreeText;
    private Button btnSaveForm;
    private TextView tvTitulo; // Nuevo título
    
    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    
    private String momentoDia = "General";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_diario);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        actvPrimaryEmotion = findViewById(R.id.actvPrimaryEmotion);
        etEmotionCause = findViewById(R.id.etEmotionCause);
        etFreeText = findViewById(R.id.etFreeText);
        btnSaveForm = findViewById(R.id.btnSaveForm);
        // Nota: Asegúrate de agregar un TextView con id 'tvTitulo' en el layout XML de activity_escribir_diario
        // Si no existe, esta línea fallará. Como no tengo el XML abierto, lo omito por seguridad
        // pero uso el Intent para guardar el dato.

        // Recuperar el momento del día (Matutino o Nocturno)
        if (getIntent().hasExtra("momento_dia")) {
            momentoDia = getIntent().getStringExtra("momento_dia");
            // Aquí podrías cambiar el título de la pantalla:
            // tvTitulo.setText("Diario " + momentoDia);
            setTitle("Diario " + momentoDia);
        }

        // Configurar el menú desplegable de emociones
        configurarDropdownEmociones();

        // Configurar listener del botón de guardar
        btnSaveForm.setOnClickListener(v -> guardarEntradaDiario());
    }

    private void configurarDropdownEmociones() {
        // Lista de emociones
        String[] emociones = new String[] {"Ira", "Disgusto", "Tristeza", "Miedo", "Sorpresa", "Felicidad"};

        // Adaptador para el menú
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                emociones
        );

        // Asignar el adaptador al AutoCompleteTextView
        actvPrimaryEmotion.setAdapter(adapter);
    }

    private void guardarEntradaDiario() {
        String emocion = actvPrimaryEmotion.getText().toString().trim();
        String causa = etEmotionCause.getText() != null ? etEmotionCause.getText().toString().trim() : "";
        String texto = etFreeText.getText() != null ? etFreeText.getText().toString().trim() : "";

        if (emocion.isEmpty()) {
            Toast.makeText(this, "Por favor, selecciona una emoción principal", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveForm.setEnabled(false); // Evitar doble clic

        // Obtener Usuario Actual
        FirebaseUser user = mAuth.getCurrentUser();
        
        if (user == null) {
             Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
             btnSaveForm.setEnabled(true);
             return;
        }

        String uid = user.getUid();

        // Crear Mapa de datos
        Map<String, Object> entrada = new HashMap<>();
        entrada.put("momento_dia", momentoDia); // <--- NUEVO CAMPO
        entrada.put("emocion_principal", emocion);
        entrada.put("causa", causa);
        entrada.put("texto_libre", texto);
        entrada.put("fecha", new Date()); 

        // Guardar en Firestore: users/{uid}/diario_emociones
        db.collection("users").document(uid).collection("diario_emociones")
                .add(entrada)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Entrada " + momentoDia + " guardada!", Toast.LENGTH_SHORT).show();
                    finish(); 
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSaveForm.setEnabled(true); 
                });
    }
}
