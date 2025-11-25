package com.dev.fellpulse_hub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class activityBienvenida extends AppCompatActivity {

    // Se ajusta la variable para que coincida con el layout
    private Button btnEmpezar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        // Se busca el botón con el ID correcto del archivo XML
        btnEmpezar = findViewById(R.id.btnEmpezar);

        // Al hacer clic en "Empezar", se va a la pantalla de Login
        btnEmpezar.setOnClickListener(v -> {
            startActivity(new Intent(activityBienvenida.this, activityLogin.class));
        });
    }
}
