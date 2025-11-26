package com.dev.fellpulse_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activityInicio extends AppCompatActivity {

    private ImageButton btnMenuHamburguesa;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;
    private LinearLayout detectorLayout, buenosHabitosLayout, malosHabitosLayout, comunidadLayout;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // --- ACTUALIZACIÓN DE BASE DE DATOS ---
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
        String[] opcionesAjustes = {"Notificaciones", "Modo Oscuro", "Editar Datos Personales", "Gestionar Emociones", "Idioma", "Política de Privacidad", "Acerca de"};
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
                        case 2: // Editar Datos Personales
                            mostrarMenuEditarDatos();
                            break;
                        case 3: // Gestionar Emociones
                            mostrarGestionEmociones();
                            break;
                        case 4: // Idioma
                             try {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS));
                            } catch (Exception e) {
                                Toast.makeText(this, "No se pudo abrir la configuración de idioma", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 5: // Política de Privacidad
                            abrirLink("https://www.fellpulsehub.com/privacy");
                            break;
                        case 6: // Acerca de
                            new AlertDialog.Builder(activityInicio.this)
                                    .setTitle("Acerca de Fellpulse Hub")
                                    .setMessage("Versión 1.0\n\nUna app para ayudarte a conectar con tus emociones.")
                                    .setPositiveButton("OK", null)
                                    .show();
                            break;
                    }
                }).show();
    }

    // --- GESTIÓN DE EMOCIONES ---
    private void mostrarGestionEmociones() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gestión de Emociones");

        db.collection("configuracion_rueda").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> nombresEmociones = new ArrayList<>();
            List<DocumentSnapshot> documentos = queryDocumentSnapshots.getDocuments();

            for (DocumentSnapshot doc : documentos) {
                nombresEmociones.add(doc.getString("nombre"));
            }
            nombresEmociones.add("+ Agregar Nueva Emoción");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombresEmociones);
            ListView listView = new ListView(this);
            listView.setAdapter(adapter);
            builder.setView(listView);

            AlertDialog dialog = builder.create();
            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (position == nombresEmociones.size() - 1) {
                    mostrarFormularioEmocion(null);
                } else {
                    mostrarFormularioEmocion(documentos.get(position));
                }
                dialog.dismiss();
            });
            dialog.show();
        }).addOnFailureListener(e -> Toast.makeText(this, "Error cargando emociones", Toast.LENGTH_SHORT).show());
    }

    private void mostrarFormularioEmocion(DocumentSnapshot doc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(doc == null ? "Nueva Emoción" : "Editar " + doc.getString("nombre"));

        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        scrollView.addView(layout);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre (ej: Calma)");
        if (doc != null) inputNombre.setText(doc.getString("nombre"));
        layout.addView(inputNombre);

        final EditText inputColor = new EditText(this);
        inputColor.setHint("Color Hex (ej: #FFFFFF)");
        if (doc != null) inputColor.setText(doc.getString("color_hex"));
        layout.addView(inputColor);

        final EditText inputMin = new EditText(this);
        inputMin.setHint("BPM Mínimo (ej: 60)");
        inputMin.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (doc != null && doc.getLong("bpm_min") != null) inputMin.setText(String.valueOf(doc.getLong("bpm_min")));
        layout.addView(inputMin);

        final EditText inputMax = new EditText(this);
        inputMax.setHint("BPM Máximo (ej: 80)");
        inputMax.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (doc != null && doc.getLong("bpm_max") != null) inputMax.setText(String.valueOf(doc.getLong("bpm_max")));
        layout.addView(inputMax);

        // SECCIÓN DE NOTAS
        TextView tvNotas = new TextView(this);
        tvNotas.setText("\n--- Notas de Ayuda (5 Tips) ---");
        tvNotas.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvNotas);

        List<EditText> inputNotas = new ArrayList<>();
        List<String> notasGuardadas = doc != null ? (List<String>) doc.get("tips") : null;

        for (int i = 0; i < 5; i++) {
            EditText nota = new EditText(this);
            nota.setHint("Nota " + (i + 1));
            if (notasGuardadas != null && i < notasGuardadas.size()) {
                nota.setText(notasGuardadas.get(i));
            }
            inputNotas.add(nota);
            layout.addView(nota);
        }

        builder.setView(scrollView);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            String color = inputColor.getText().toString().trim();
            String minStr = inputMin.getText().toString().trim();
            String maxStr = inputMax.getText().toString().trim();

            if (nombre.isEmpty() || color.isEmpty() || minStr.isEmpty() || maxStr.isEmpty()) {
                Toast.makeText(this, "Todos los campos principales son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> nuevasNotas = new ArrayList<>();
            for (EditText et : inputNotas) {
                String textoNota = et.getText().toString().trim();
                if (!textoNota.isEmpty()) {
                    nuevasNotas.add(textoNota);
                } else {
                    nuevasNotas.add("Sin consejo asignado."); // Relleno para mantener 5
                }
            }

            Map<String, Object> emocionData = new HashMap<>();
            emocionData.put("nombre", nombre);
            emocionData.put("color_hex", color);
            emocionData.put("bpm_min", Long.parseLong(minStr));
            emocionData.put("bpm_max", Long.parseLong(maxStr));
            emocionData.put("descripcion", "Configurada por usuario");
            emocionData.put("tips", nuevasNotas); // Guardamos las 5 notas

            String docId = doc != null ? doc.getId() : nombre.toLowerCase();

            db.collection("configuracion_rueda").document(docId).set(emocionData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Emoción y notas guardadas", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
        });

        if (doc != null) {
            builder.setNeutralButton("Eliminar", (dialog, which) -> {
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar Emoción")
                        .setMessage("¿Seguro que deseas eliminar " + doc.getString("nombre") + "?")
                        .setPositiveButton("Sí", (d, w) -> {
                            db.collection("configuracion_rueda").document(doc.getId()).delete()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Eliminada", Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    // --- FIN GESTIÓN EMOCIONES ---

    // --- FUNCIONALIDAD EDITAR DATOS ---

    private void mostrarMenuEditarDatos() {
        String[] opcionesEdicion = {"Cambiar Nombre", "Cambiar Apellido", "Cambiar Contraseña", "Eliminar Cuenta"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Datos Personales");
        
        builder.setItems(opcionesEdicion, (dialog, which) -> {
            switch (which) {
                case 0: mostrarDialogoCambio("Nombre"); break;
                case 1: mostrarDialogoCambio("Apellido"); break;
                case 2: mostrarDialogoCambio("Contraseña"); break;
                case 3: confirmarEliminacionCuenta(); break;
            }
        });
        
        builder.setNegativeButton("Regresar", null);
        builder.show();
    }

    private void confirmarEliminacionCuenta() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Cuenta")
                .setMessage("¿Estás seguro? Esta acción borrará todos tus datos y no se puede deshacer.")
                .setPositiveButton("Sí", (dialog, which) -> eliminarCuenta())
                .setNegativeButton("No", (dialog, which) -> mostrarMenuEditarDatos()) // Vuelve al menú si dice No
                .show();
    }

    private void eliminarCuenta() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // 1. Borrar datos de Firestore
        db.collection("users").document(user.getUid()).delete();

        // 2. Borrar autenticación
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cuenta eliminada correctamente.", Toast.LENGTH_SHORT).show();
                
                // Limpiar datos locales
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Ir al login
                Intent intent = new Intent(activityInicio.this, activityLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                String error = task.getException() != null ? task.getException().getMessage() : "Error";
                if (error.toLowerCase().contains("recent login") || error.toLowerCase().contains("sensitive")) {
                    Toast.makeText(this, "POR SEGURIDAD: Debes cerrar sesión e ingresar de nuevo para poder eliminar tu cuenta.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "No se pudo eliminar la cuenta: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void mostrarDialogoCambio(String tipo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar " + tipo);

        // Crear layout para el input
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText input = new EditText(this);
        // Configurar tipo de entrada según la opción
        if (tipo.equals("Contraseña")) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        
        layout.addView(input);
        builder.setView(layout);

        builder.setPositiveButton("Confirmar Cambios", (dialog, which) -> {
            String nuevoValor = input.getText().toString().trim();
            if (!nuevoValor.isEmpty()) {
                procesarCambioDatos(tipo, nuevoValor);
            } else {
                Toast.makeText(this, "El campo no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Regresar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void procesarCambioDatos(String tipo, String valor) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = user.getUid();

        switch (tipo) {
            case "Nombre":
                // Actualizar en Firestore
                db.collection("users").document(uid).update("nombre", valor)
                        .addOnSuccessListener(aVoid -> {
                            // Actualizar SharedPreferences
                            getSharedPreferences("user_prefs", MODE_PRIVATE).edit().putString("user_name", valor).apply();
                            Toast.makeText(this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar nombre", Toast.LENGTH_SHORT).show());
                break;

            case "Apellido":
                // Actualizar en Firestore
                db.collection("users").document(uid).update("apellido", valor)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Apellido actualizado correctamente", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar apellido", Toast.LENGTH_SHORT).show());
                break;

            case "Contraseña":
                // Actualizar Auth (Login)
                if (valor.length() < 6) {
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.updatePassword(valor).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Actualizar Firestore (Como pediste anteriormente)
                        db.collection("users").document(uid).update("password", valor);
                        Toast.makeText(this, "Contraseña actualizada exitosamente.", Toast.LENGTH_SHORT).show();
                    } else {
                         String error = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        // Manejo específico del error de autenticación reciente
                        if (error.toLowerCase().contains("recent login") || error.toLowerCase().contains("sensitive")) {
                            Toast.makeText(this, "POR SEGURIDAD: Debes Cerrar Sesión e ingresar de nuevo para cambiar la contraseña.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
        }
    }

    // --- FIN FUNCIONALIDAD EDITAR DATOS ---

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
