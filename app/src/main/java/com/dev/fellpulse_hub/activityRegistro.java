package com.dev.fellpulse_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class activityRegistro extends AppCompatActivity {

    private EditText edtNombre, edtApellido, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegistrar;
    private TextView tvLoginLink;
    
    // Variables Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        edtNombre = findViewById(R.id.inputNombre);
        edtApellido = findViewById(R.id.inputApellido);
        edtEmail = findViewById(R.id.inputEmail);
        edtPassword = findViewById(R.id.inputPassword);
        edtConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnRegistrar = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegistrar.setOnClickListener(v -> {
            String nombre = edtNombre.getText().toString().trim();
            String apellido = edtApellido.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            registrarEnFirebase(email, password, nombre, apellido);
        });

        tvLoginLink.setOnClickListener(v -> {
            finish();
        });
    }

    private void registrarEnFirebase(String email, String password, String nombre, String apellido) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Usuario creado en Auth
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // PASO CLAVE: Pasamos también la contraseña para guardarla
                            guardarDatosFirestore(user.getUid(), email, nombre, apellido, password);
                        }
                    } else {
                        Toast.makeText(activityRegistro.this, "Fallo en registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarDatosFirestore(String uid, String email, String nombre, String apellido, String password) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);
        userMap.put("nombre", nombre);
        userMap.put("apellido", apellido);
        userMap.put("email", email);
        // ADVERTENCIA: Guardar contraseñas en texto plano no es recomendado por seguridad.
        // Se hace aquí bajo petición explícita del usuario.
        userMap.put("password", password); 
        userMap.put("fecha_registro", com.google.firebase.Timestamp.now());

        db.collection("users").document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // Guardar localmente también para acceso rápido
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_logged_in", true);
                    editor.putString("user_email", email);
                    editor.putString("user_name", nombre); // Guardamos nombre para los posts
                    editor.putString("user_uid", uid);
                    editor.apply();

                    Toast.makeText(activityRegistro.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(activityRegistro.this, activityInicio.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activityRegistro.this, "Error guardando datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
