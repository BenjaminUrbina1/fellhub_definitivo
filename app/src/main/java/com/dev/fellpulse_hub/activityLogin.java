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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class activityLogin extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegistrar;
    
    // Firebase y SharedPreferences
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        edtEmail = findViewById(R.id.inputEmail);
        edtPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnlogin);
        tvRegistrar = findViewById(R.id.tvRegistrar);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            loginConFirebase(email, password);
        });

        tvRegistrar.setOnClickListener(v -> {
            startActivity(new Intent(activityLogin.this, activityRegistro.class));
        });
    }

    private void loginConFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            recuperarDatosUsuario(user.getUid(), email);
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        Toast.makeText(activityLogin.this, "Error de inicio de sesión: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void recuperarDatosUsuario(String uid, String email) {
        // Buscar los datos extra (nombre) en Firestore
        db.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    String nombreUsuario = "Usuario"; // Valor por defecto
                    
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            nombreUsuario = document.getString("nombre");
                            if (nombreUsuario == null) nombreUsuario = "Usuario";
                        }
                    }

                    // Guardar en SharedPreferences para uso local
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_logged_in", true);
                    editor.putString("user_email", email);
                    editor.putString("user_name", nombreUsuario);
                    editor.putString("user_uid", uid);
                    editor.apply();

                    // Ir al inicio
                    Intent intent = new Intent(activityLogin.this, activityInicio.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }
}
