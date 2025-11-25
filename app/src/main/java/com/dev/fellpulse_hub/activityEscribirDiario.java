package com.dev.fellpulse_hub;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class activityEscribirDiario extends AppCompatActivity {

    private AutoCompleteTextView actvPrimaryEmotion;
    private TextInputEditText etEmotionCause, etFreeText;
    private Button btnSaveForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_diario);

        // Inicializar vistas
        actvPrimaryEmotion = findViewById(R.id.actvPrimaryEmotion);
        etEmotionCause = findViewById(R.id.etEmotionCause);
        etFreeText = findViewById(R.id.etFreeText);
        btnSaveForm = findViewById(R.id.btnSaveForm);

        // Configurar el menú desplegable de emociones
        configurarDropdownEmociones();

        // Configurar listener del botón de guardar
        btnSaveForm.setOnClickListener(v -> {
            Toast.makeText(this, "Entrada guardada", Toast.LENGTH_SHORT).show();
            finish();
        });
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
}
