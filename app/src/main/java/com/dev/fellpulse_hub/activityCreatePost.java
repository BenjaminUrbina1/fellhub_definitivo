package com.dev.fellpulse_hub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class activityCreatePost extends AppCompatActivity {

    private TextInputEditText etPostTitle, etPostContent;
    private ImageView ivSelectedImage;
    private Button btnSelectImage, btnPublish;

    // Claves para pasar los datos de vuelta
    public static final String EXTRA_TITLE = "com.dev.fellpulse_hub.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "com.dev.fellpulse_hub.EXTRA_CONTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        inicializarVistas();
        configurarListeners();
    }

    private void inicializarVistas() {
        etPostTitle = findViewById(R.id.etPostTitle);
        etPostContent = findViewById(R.id.etPostContent);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnPublish = findViewById(R.id.btnPublish);
    }

    private void configurarListeners() {
        btnSelectImage.setOnClickListener(v -> {
            Toast.makeText(this, "Abriendo galería...", Toast.LENGTH_SHORT).show();
        });

        btnPublish.setOnClickListener(v -> {
            String title = etPostTitle.getText().toString().trim();
            String content = etPostContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Por favor, completa el título y el contenido", Toast.LENGTH_SHORT).show();
            } else {
                // 1. Crear un Intent para devolver los datos
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_TITLE, title);
                resultIntent.putExtra(EXTRA_CONTENT, content);

                // 2. Establecer el resultado como "OK" y adjuntar los datos
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
