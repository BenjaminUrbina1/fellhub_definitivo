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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class activityEstadisticas extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout btnHeart;
    private MaterialButtonToggleGroup toggleGroup;
    private LineChart lineChart;
    private TextView tvHeartRatePercentage, tvOxygenSaturationPercentage, tvStatsTitle, tvMoodMessage;
    private ImageView ivMoodIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        inicializarVistas();
        configurarNavegacion();
        configurarFiltros();
        configurarGrafico();
        cargarDatos("Semanales");

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
                return true; // Ya estamos aquí
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
                    cargarDatos("Semanales");
                } else if (checkedId == R.id.btnMonthly) {
                    tvStatsTitle.setText("Estadísticas Mensuales");
                    cargarDatos("Mensuales");
                } else if (checkedId == R.id.btnYearly) {
                    tvStatsTitle.setText("Estadísticas Anuales");
                    cargarDatos("Anuales");
                }
            }
        });
    }

    private void configurarGrafico() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("Cargando datos...");
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
    }

    private void cargarDatos(String periodo) {
        int cantidadDatos;
        if (periodo.equals("Semanales")) {
            cantidadDatos = 7;
        } else if (periodo.equals("Mensuales")) {
            cantidadDatos = 30;
        } else { // Anuales
            cantidadDatos = 12;
        }

        List<Entry> entriesHeartRate = new ArrayList<>();
        List<Entry> entriesOxygen = new ArrayList<>();
        Random random = new Random();

        float totalHeartRate = 0;
        float totalOxygen = 0;

        for (int i = 0; i < cantidadDatos; i++) {
            float heartRate = 60 + random.nextFloat() * 40; // Rango 60-100
            float oxygen = 95 + random.nextFloat() * 5;     // Rango 95-100
            entriesHeartRate.add(new Entry(i, heartRate));
            entriesOxygen.add(new Entry(i, oxygen));
            totalHeartRate += heartRate;
            totalOxygen += oxygen;
        }

        float avgHeartRate = totalHeartRate / cantidadDatos;
        float avgOxygen = totalOxygen / cantidadDatos;

        tvHeartRatePercentage.setText(String.format("%.1f %%", avgHeartRate));
        tvOxygenSaturationPercentage.setText(String.format("%.1f %%", avgOxygen));

        actualizarMensajeAnimo(avgHeartRate, avgOxygen);

        LineDataSet dataSetHeartRate = new LineDataSet(entriesHeartRate, "Frec. Cardíaca");
        dataSetHeartRate.setColor(Color.RED);
        dataSetHeartRate.setCircleColor(Color.RED);

        LineDataSet dataSetOxygen = new LineDataSet(entriesOxygen, "Sat. Oxígeno");
        dataSetOxygen.setColor(Color.BLUE);
        dataSetOxygen.setCircleColor(Color.BLUE);

        LineData lineData = new LineData(dataSetHeartRate, dataSetOxygen);
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refrescar gráfico
    }

    private void actualizarMensajeAnimo(float avgHeartRate, float avgOxygen) {
        if (avgHeartRate > 90 || avgHeartRate < 70) {
            tvMoodMessage.setText("Tus niveles de estrés parecen altos. ¡Tómate un descanso!");
            ivMoodIcon.setImageResource(R.drawable.malos);
        } else if (avgOxygen < 96) {
            tvMoodMessage.setText("Tu oxígeno es un poco bajo. Asegúrate de estar en un lugar ventilado.");
            ivMoodIcon.setImageResource(R.drawable.malos);
        } else {
            tvMoodMessage.setText("¡Tus estadísticas se ven geniales! Sigue así.");
            ivMoodIcon.setImageResource(R.drawable.buenos);
        }
    }
}
