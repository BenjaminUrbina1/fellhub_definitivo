package com.dev.fellpulse_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // <-- IMPORT NECESARIO
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class activityRegistro extends AppCompatActivity {

    private EditText edtNombre, edtApellido, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegistrar;
    private TextView tvLoginLink; // <-- VARIABLE PARA EL TEXTO
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        edtNombre = findViewById(R.id.inputNombre);
        edtApellido = findViewById(R.id.inputApellido);
        edtEmail = findViewById(R.id.inputEmail);
        edtPassword = findViewById(R.id.inputPassword);
        edtConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnRegistrar = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink); // <-- INICIALIZACIÓN DEL TEXTO

        btnRegistrar.setOnClickListener(v -> {
            String nombre = edtNombre.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", true);
            editor.putString("user_name", nombre);
            editor.putString("user_email", email);
            editor.apply();

            startActivity(new Intent(activityRegistro.this, activityInicio.class));
            finish();
        });

        // --- ACCIÓN PARA EL TEXTO "INICIA SESIÓN" ---
        tvLoginLink.setOnClickListener(v -> {
            // Cierra la actividad de registro para volver a la de login
            finish();
        });
    }
}
