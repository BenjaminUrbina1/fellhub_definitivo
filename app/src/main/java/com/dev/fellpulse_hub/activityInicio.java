package com.dev.fellpulse_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class activityInicio extends AppCompatActivity {

    private ImageButton btnMenuHamburguesa;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;
    private LinearLayout detectorLayout, buenosHabitosLayout, malosHabitosLayout, comunidadLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // --- ACTUALIZACIÓN DE BASE DE DATOS ---
        // Esto cargará la configuración de la Rueda de Emociones en Firebase
        // la primera vez que se ejecute.
        InicializadorDatos.cargarConfiguracionInicial();
        // --------------------------------------

        inicializarVistas();
        configurarMenuHamburguesa();
        configurarBottomNavigation();
        configurarClicksContenido();
        configurarBotonCorazon();

        Animation scaleDownAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        btnHeart.startAnimation(scaleDownAnimation);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(activityInicio.this)
                        .setTitle("Salir")
                        .setMessage("¿Deseas cerrar la aplicación?")
                        .setPositiveButton("Sí", (dialog, which) -> finishAffinity())
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void inicializarVistas() {
        btnMenuHamburguesa = findViewById(R.id.btnMenuHamburguesa);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);
        detectorLayout = findViewById(R.id.detector);
        buenosHabitosLayout = findViewById(R.id.BuenosHabitos);
        malosHabitosLayout = findViewById(R.id.MalosHabitos);
        comunidadLayout = findViewById(R.id.Comunidad);
    }

    private void configurarBotonCorazon() {
        btnHeart.setOnClickListener(v -> {
            Toast.makeText(this, "Ya estás en la pantalla principal", Toast.LENGTH_SHORT).show();
        });
    }

    private void configurarClicksContenido() {
        detectorLayout.setOnClickListener(v -> startActivity(new Intent(activityInicio.this, activityDetectorEmociones.class)));
        buenosHabitosLayout.setOnClickListener(v -> startActivity(new Intent(activityInicio.this, activityHabitosSaludables.class)));
        malosHabitosLayout.setOnClickListener(v -> startActivity(new Intent(activityInicio.this, activityHabitosNoSaludables.class)));
        comunidadLayout.setOnClickListener(v -> startActivity(new Intent(activityInicio.this, activityComunidad.class)));
    }

    private void configurarBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, activityCuenta.class));
            } else if (id == R.id.nav_creators) {
                startActivity(new Intent(this, activityCreadores.class));
            } else if (id == R.id.nav_statistics) {
                startActivity(new Intent(this, activityEstadisticas.class));
            } else if (id == R.id.nav_daily_aura) {
                startActivity(new Intent(this, activityDiario.class));
            }
            return true;
        });
    }
    
    private void configurarMenuHamburguesa() {
        btnMenuHamburguesa.setOnClickListener(v -> mostrarOpcionesHamburguesa());
    }

    private void mostrarOpcionesHamburguesa() {
        String[] opciones = {"Ajustes", "Redes Sociales", "Cerrar Sesión"};
        new AlertDialog.Builder(this)
                .setTitle("Menú")
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: mostrarAjustes(); break;
                        case 1: mostrarRedesSociales(); break;
                        case 2: cerrarSesion(); break;
                    }
                }).show();
    }

    private void mostrarAjustes() {
        String[] opcionesAjustes = {"Notificaciones", "Modo Oscuro", "Cambiar Contraseña", "Idioma", "Política de Privacidad", "Acerca de"};
        new AlertDialog.Builder(this)
                .setTitle("Ajustes")
                .setItems(opcionesAjustes, (dialog, which) -> {
                    switch (which) {
                        case 0: // Notificaciones
                            try {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, getPackageName());
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(this, "No se pudo abrir la configuración de notificaciones", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1: // Modo Oscuro
                            Toast.makeText(this, "Función en desarrollo", Toast.LENGTH_SHORT).show();
                            break;
                        case 2: // Cambiar Contraseña
                            Toast.makeText(this, "Función en desarrollo", Toast.LENGTH_SHORT).show();
                            break;
                        case 3: // Idioma
                             try {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS));
                            } catch (Exception e) {
                                Toast.makeText(this, "No se pudo abrir la configuración de idioma", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 4: // Política de Privacidad
                            abrirLink("https://www.fellpulsehub.com/privacy");
                            break;
                        case 5: // Acerca de
                            new AlertDialog.Builder(activityInicio.this)
                                    .setTitle("Acerca de Fellpulse Hub")
                                    .setMessage("Versión 1.0\n\nUna app para ayudarte a conectar con tus emociones.")
                                    .setPositiveButton("OK", null)
                                    .show();
                            break;
                    }
                }).show();
    }

    private void mostrarRedesSociales() {
        String[] redes = {"Instagram", "Facebook", "Twitter"};
        new AlertDialog.Builder(this)
                .setTitle("Síguenos en")
                .setItems(redes, (dialog, which) -> {
                    String url = "";
                    switch (which) {
                        case 0: url = "https://instagram.com/fellpulsehub"; break;
                        case 1: url = "https://facebook.com/fellpulsehub"; break;
                        case 2: url = "https://twitter.com/fellpulsehub"; break;
                    }
                    abrirLink(url);
                }).show();
    }

    private void abrirLink(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir el enlace", Toast.LENGTH_SHORT).show();
        }
    }

    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Seguro que deseas salir?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(activityInicio.this, activityLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }).setNegativeButton("No", null).show();
    }
}
