package com.dev.fellpulse_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // <-- IMPORT NECESARIO
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class activityLogin extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegistrar; // <-- VARIABLE PARA EL TEXTO
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        edtEmail = findViewById(R.id.inputEmail);
        edtPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnlogin);
        tvRegistrar = findViewById(R.id.tvRegistrar); // <-- INICIALIZACIÓN DEL TEXTO

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", true);
            editor.putString("user_email", email);
            editor.putString("user_name", "Usuario"); // Nombre genérico
            editor.apply();

            startActivity(new Intent(activityLogin.this, activityInicio.class));
            finish();
        });

        // --- ACCIÓN PARA EL TEXTO "REGÍSTRATE AQUÍ" ---
        tvRegistrar.setOnClickListener(v -> {
            startActivity(new Intent(activityLogin.this, activityRegistro.class));
        });
    }
}
