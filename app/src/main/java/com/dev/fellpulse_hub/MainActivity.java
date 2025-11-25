package com.dev.fellpulse_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Devolvemos el layout a la actividad para que tenga una interfaz
        setContentView(R.layout.activity_main);

        // 2. Mantenemos la lógica de sesión para que se ejecute en segundo plano
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

            Intent intent;
            if (isLoggedIn) {
                // Si hay sesión iniciada, ir directamente a la pantalla de Inicio
                intent = new Intent(MainActivity.this, activityInicio.class);
            } else {
                // Si no hay sesión, iniciar el flujo de bienvenida/login
                intent = new Intent(MainActivity.this, activityBienvenida.class);
            }

            startActivity(intent);
            finish(); // Cierra esta actividad para que el usuario no pueda volver a ella
        }, 1500); // Un pequeño retraso para mostrar la pantalla de bienvenida
    }
}
