package com.dev.fellpulse_hub;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class activityComunidad extends AppCompatActivity {

    private static final int CREATE_POST_REQUEST = 1;

    private RecyclerView rvPosts;
    private FloatingActionButton fabCreatePost;
    private FrameLayout btnHeart;
    private BottomNavigationView bottomNavigationView;

    private List<Post> postList;
    private PostAdapter postAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunidad);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        inicializarVistas();
        configurarListeners();
        configurarRecyclerView();
        configurarBottomNavigation();

        // Cargar los posts desde Firebase
        escucharPostsEnTiempoReal();

        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);
    }

    private void inicializarVistas() {
        rvPosts = findViewById(R.id.rvPosts);
        fabCreatePost = findViewById(R.id.fabCreatePost);
        btnHeart = findViewById(R.id.btnHeart);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void configurarListeners() {
        fabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(activityComunidad.this, activityCreatePost.class);
            // Ya no necesitamos forzosamente startActivityForResult si leemos en tiempo real,
            // pero lo mantenemos para no romper el flujo si el usuario espera volver.
            startActivity(intent);
        });

        btnHeart.setOnClickListener(v -> {
            Intent intent = new Intent(activityComunidad.this, activityInicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
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

    private void configurarRecyclerView() {
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(postAdapter);
    }

    private void escucharPostsEnTiempoReal() {
        // Escuchar cambios en la colección "publicaciones" ordenados por fecha descendente
        db.collection("publicaciones")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error al cargar posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        // Una forma eficiente es procesar solo los cambios
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    // Convertir el documento a objeto Post
                                    // Nota: Asegúrate de que la clase Post tenga constructor vacío o manejes el mapeo manualmente
                                    // Aquí lo hacemos manualmente para mapear los nombres de campos de Firebase a tu clase Post
                                    String userName = dc.getDocument().getString("nombre_usuario");
                                    String title = dc.getDocument().getString("titulo");
                                    String content = dc.getDocument().getString("contenido");
                                    
                                    Post newPost = new Post(userName, title, content);
                                    
                                    // Insertar en la lista. Como usamos OrderBy DESC, los nuevos llegan primero si es carga inicial,
                                    // pero para actualizaciones en tiempo real de nuevos items, insertamos al inicio o según índice.
                                    // Simplificación: agregamos y notificamos.
                                    // Para mantener el orden correcto visualmente al recibir actualizaciones,
                                    // insertamos en la posición correcta (dc.getNewIndex())
                                    postList.add(dc.getNewIndex(), newPost);
                                    postAdapter.notifyItemInserted(dc.getNewIndex());
                                    break;
                                    
                                case MODIFIED:
                                    // Lógica para actualizar si se edita un post (opcional por ahora)
                                    break;
                                    
                                case REMOVED:
                                    // Lógica para eliminar (opcional por ahora)
                                    break;
                            }
                        }
                        
                        // Si la lista está vacía (primera vez sin internet o sin datos), mostrar mensaje bienvenida
                        if (postList.isEmpty()) {
                             postList.add(new Post("Fellpulse Team", "¡Bienvenido a la comunidad!", "Sé el primero en escribir algo."));
                             postAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
