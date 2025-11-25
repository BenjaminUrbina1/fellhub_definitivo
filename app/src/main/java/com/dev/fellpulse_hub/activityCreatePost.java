package com.dev.fellpulse_hub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activityCreatePost extends AppCompatActivity {

    private TextInputEditText etPostTitle, etPostContent;
    private ImageView ivSelectedImage;
    private Button btnSelectImage, btnPublish;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
            Toast.makeText(this, "Abriendo galería... (Próximamente)", Toast.LENGTH_SHORT).show();
        });

        btnPublish.setOnClickListener(v -> {
            String title = etPostTitle.getText().toString().trim();
            String content = etPostContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Por favor, completa el título y el contenido", Toast.LENGTH_SHORT).show();
                return;
            }

            guardarPostEnFirestore(title, content);
        });
    }

    private void guardarPostEnFirestore(String title, String content) {
        btnPublish.setEnabled(false); // Desactivar botón para evitar doble clic

        // Recuperar nombre de usuario (si hay Auth usamos el real, sino fallback a SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Usuario Anónimo");
        
        // Intentar obtener UID real
        String userId;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // Fallback si por alguna razón no hay Auth (ej: sesión vieja)
            userId = prefs.getString("user_uid", "anonimo");
        }

        // Crear mapa de datos para Firestore
        Map<String, Object> post = new HashMap<>();
        post.put("titulo", title);
        post.put("contenido", content);
        post.put("nombre_usuario", userName);
        post.put("usuario_id", userId); // Ahora guardamos el UID real
        post.put("fecha", new Date());

        // Guardar en colección "publicaciones"
        db.collection("publicaciones")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Publicado con éxito!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al publicar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnPublish.setEnabled(true);
                });
    }
}
