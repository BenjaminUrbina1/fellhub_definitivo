package com.dev.fellpulse_hub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class activityEstadisticas extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;
    private MaterialButtonToggleGroup toggleGroup;
    private LineChart lineChart;
    private TextView tvHeartRatePercentage, tvOxygenSaturationPercentage, tvStatsTitle, tvMoodMessage;
    private ImageView ivMoodIcon;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        inicializarVistas();
        configurarNavegacion();
        configurarFiltros();
        configurarGrafico();
        
        // Cargar datos por defecto (Semanales)
        cargarDatosDeFirebase("Semanales");

        Animation heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heart_beat);
        btnHeart.startAnimation(heartBeatAnimation);
    }

    private void inicializarVistas() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        btnHeart = findViewById(R.id.btnHeart);
        toggleGroup = findViewById(R.id.toggleGroupDateFilter);
        lineChart = findViewById(R.id.lineChart);
        tvHeartRatePercentage = findViewById(R.id.tvHeartRatePercentage);
        tvOxygenSaturationPercentage = findViewById(R.id.tvOxygenSaturationPercentage);
        tvStatsTitle = findViewById(R.id.tvStatsTitle);
        tvMoodMessage = findViewById(R.id.tvMoodMessage);
        ivMoodIcon = findViewById(R.id.ivMoodIcon);
    }

    private void configurarNavegacion() {
        btnHeart.setOnClickListener(v -> {
            Intent intent = new Intent(activityEstadisticas.this, activityInicio.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_statistics);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_user_profile) {
                startActivity(new Intent(this, activityCuenta.class));
            } else if (id == R.id.nav_creators) {
                startActivity(new Intent(this, activityCreadores.class));
            } else if (id == R.id.nav_statistics) {
                return true; 
            } else if (id == R.id.nav_daily_aura) {
                startActivity(new Intent(this, activityDiario.class));
            }
            return true;
        });
    }

    private void configurarFiltros() {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnWeekly) {
                    tvStatsTitle.setText("Estadísticas Semanales");
                    cargarDatosDeFirebase("Semanales");
                } else if (checkedId == R.id.btnMonthly) {
                    tvStatsTitle.setText("Estadísticas Mensuales");
                    cargarDatosDeFirebase("Mensuales");
                } else if (checkedId == R.id.btnYearly) {
                    tvStatsTitle.setText("Estadísticas Anuales");
                    cargarDatosDeFirebase("Anuales");
                }
            }
        });
    }

    private void configurarGrafico() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("No hay registros disponibles aún.");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
    }

    private void cargarDatosDeFirebase(String periodo) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            lineChart.setNoDataText("Inicia sesión para ver tus estadísticas.");
            lineChart.invalidate();
            return;
        }

        int limite = 7;
        if (periodo.equals("Mensuales")) limite = 30;
        if (periodo.equals("Anuales")) limite = 365; // O las últimas 365 mediciones

        // Consulta a la colección "historial_emociones"
        // Ordenamos por fecha descendente para obtener los ÚLTIMOS registros
        db.collection("historial_emociones")
                .whereEqualTo("usuario_id", user.getUid())
                .orderBy("fecha", Query.Direction.DESCENDING)
                .limit(limite)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Entry> entriesHeartRate = new ArrayList<>();
                    List<Entry> entriesOxygen = new ArrayList<>();
                    
                    if (queryDocumentSnapshots.isEmpty()) {
                        lineChart.clear();
                        lineChart.setNoDataText("Sin datos del sensor aún.");
                        tvHeartRatePercentage.setText("--");
                        tvOxygenSaturationPercentage.setText("--");
                        return;
                    }

                    // Recorremos los documentos
                    // Nota: Vienen del más nuevo al más viejo. Para el gráfico queremos cronológico (viejo -> nuevo)
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    Collections.reverse(docs); // Invertimos la lista

                    float totalHeartRate = 0;
                    float totalOxygen = 0;
                    int count = 0;

                    for (int i = 0; i < docs.size(); i++) {
                        DocumentSnapshot doc = docs.get(i);
                        
                        // Extraer datos anidados del mapa "biometria"
                        Map<String, Object> biometria = (Map<String, Object>) doc.get("biometria");
                        
                        if (biometria != null) {
                            // Convertir a float de forma segura
                            Object bpmObj = biometria.get("bpm");
                            Object spo2Obj = biometria.get("spo2");

                            if (bpmObj != null && spo2Obj != null) {
                                float heartRate = Float.parseFloat(bpmObj.toString());
                                float oxygen = Float.parseFloat(spo2Obj.toString());

                                entriesHeartRate.add(new Entry(i, heartRate));
                                entriesOxygen.add(new Entry(i, oxygen));

                                totalHeartRate += heartRate;
                                totalOxygen += oxygen;
                                count++;
                            }
                        }
                    }

                    if (count > 0) {
                        actualizarGrafico(entriesHeartRate, entriesOxygen);
                        actualizarPromedios(totalHeartRate / count, totalOxygen / count);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error cargando estadísticas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void actualizarGrafico(List<Entry> heartRateData, List<Entry> oxygenData) {
        LineDataSet dataSetHeartRate = new LineDataSet(heartRateData, "Frec. Cardíaca (BPM)");
        dataSetHeartRate.setColor(Color.RED);
        dataSetHeartRate.setCircleColor(Color.RED);
        dataSetHeartRate.setLineWidth(2f);
        dataSetHeartRate.setCircleRadius(3f);

        LineDataSet dataSetOxygen = new LineDataSet(oxygenData, "Sat. Oxígeno (%)");
        dataSetOxygen.setColor(Color.BLUE);
        dataSetOxygen.setCircleColor(Color.BLUE);
        dataSetOxygen.setLineWidth(2f);
        dataSetOxygen.setCircleRadius(3f);

        LineData lineData = new LineData(dataSetHeartRate, dataSetOxygen);
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refrescar
        lineChart.animateX(1000);
    }

    private void actualizarPromedios(float avgHeartRate, float avgOxygen) {
        tvHeartRatePercentage.setText(String.format("%.0f BPM", avgHeartRate));
        tvOxygenSaturationPercentage.setText(String.format("%.0f %%", avgOxygen));

        if (avgHeartRate > 100 || avgHeartRate < 60) {
            tvMoodMessage.setText("Tu ritmo cardíaco ha estado inusual. ¿Todo bien?");
            ivMoodIcon.setImageResource(R.drawable.malos);
        } else if (avgOxygen < 95) {
            tvMoodMessage.setText("Niveles de oxígeno un poco bajos.");
            ivMoodIcon.setImageResource(R.drawable.malos);
        } else {
            tvMoodMessage.setText("¡Tus estadísticas están estables!");
            ivMoodIcon.setImageResource(R.drawable.buenos);
        }
    }
}
