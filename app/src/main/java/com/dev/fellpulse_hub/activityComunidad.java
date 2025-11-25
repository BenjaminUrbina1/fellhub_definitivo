package com.dev.fellpulse_hub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class activityComunidad extends AppCompatActivity {

    private static final int CREATE_POST_REQUEST = 1;
    private static final String PREFS_NAME = "post_prefs";
    private static final String POSTS_KEY = "posts_list";

    private RecyclerView rvPosts;
    private FloatingActionButton fabCreatePost;
    private FrameLayout btnHeart;
    private BottomNavigationView bottomNavigationView;

    private List<Post> postList;
    private PostAdapter postAdapter;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunidad);

        inicializarVistas();
        configurarListeners();
        configurarRecyclerView();
        configurarBottomNavigation();

        // Cargar los posts guardados al iniciar
        loadPosts();

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
            startActivityForResult(intent, CREATE_POST_REQUEST);
        });

        btnHeart.setOnClickListener(v -> {
            Intent intent = new Intent(activityComunidad.this, activityInicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void configurarBottomNavigation() {
        // Como "Comunidad" no está en la barra, no se selecciona ningún ítem.
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
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(postAdapter);
    }

    private void loadPosts() {
        String json = sharedPreferences.getString(POSTS_KEY, null);
        Type type = new TypeToken<ArrayList<Post>>() {}.getType();
        List<Post> loadedPosts = gson.fromJson(json, type);

        if (loadedPosts != null && !loadedPosts.isEmpty()) {
            postList.clear();
            postList.addAll(loadedPosts);
        } else {
            // Si no hay posts guardados, añadir el de bienvenida
            postList.clear();
            postList.add(new Post("Fellpulse Team", "¡Bienvenido a la comunidad!", "Este es un espacio para compartir tus ideas y apoyarnos mutuamente."));
        }
        postAdapter.notifyDataSetChanged();
    }

    private void savePosts() {
        String json = gson.toJson(postList);
        sharedPreferences.edit().putString(POSTS_KEY, json).apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_POST_REQUEST && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra(activityCreatePost.EXTRA_TITLE);
            String content = data.getStringExtra(activityCreatePost.EXTRA_CONTENT);

            Post newPost = new Post("Usuario", title, content);
            postList.add(0, newPost);
            postAdapter.notifyItemInserted(0);
            rvPosts.scrollToPosition(0);

            // Guardar la lista actualizada
            savePosts();
        }
    }
}
