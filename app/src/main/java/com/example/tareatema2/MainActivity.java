package com.example.tareatema2;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etMonto;
    private TextView tvResultado;
    private SeekBar seekBarGradient;
    private View zoneGradient, zoneExit, zoneClear;

    private float inicialYGradient;
    private float inicialXExit;
    private float inicialXClear;
    private int nivelDegradado = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMonto = findViewById(R.id.etMonto);
        tvResultado = findViewById(R.id.tvResultado);
        seekBarGradient = findViewById(R.id.seekBarGradient);
        zoneGradient = findViewById(R.id.zoneGradient);
        zoneExit = findViewById(R.id.zoneExit);
        zoneClear = findViewById(R.id.zoneClear);

        actualizarDegradado(nivelDegradado);

        etMonto.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                calcularBilletesYMonedas();
                return true;
            }
            return false;
        });

        seekBarGradient.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                nivelDegradado = progress;
                actualizarDegradado(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        setupGestures();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            calcularBilletesYMonedas();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void calcularBilletesYMonedas() {
        String texto = etMonto.getText().toString().trim();
        if (texto.isEmpty()) {
            return;
        }

        try {
            int monto = Integer.parseInt(texto);

            int b100 = monto / 100;
            monto %= 100;

            int b50 = monto / 50;
            monto %= 50;

            int b20 = monto / 20;
            monto %= 20;

            int centavosTotales = monto * 100;
            int m10Cents = centavosTotales / 10;
            centavosTotales %= 10;
            int m5Cents = centavosTotales / 5;

            String res = "(De 100: " + b100 + " (De 50: " + b50 + " (De 20: " + b20 +
                    " (De 10: " + m10Cents + " (De 5: " + m5Cents;

            tvResultado.setText(res);

        } catch (NumberFormatException ignored) {}
    }

    private void actualizarDegradado(int progress) {
        int valorGris = (int) (255 - (progress * 2.55));
        int colorMedio = Color.rgb(valorGris, valorGris, valorGris);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { Color.WHITE, colorMedio, Color.BLACK }
        );

        zoneGradient.setBackground(gd);
    }

    private void setupGestures() {
        zoneGradient.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    inicialYGradient = event.getY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float actualY = event.getY();
                    float deltaY = inicialYGradient - actualY;

                    int nuevoNivel = nivelDegradado + (int) (deltaY / 5);

                    if (nuevoNivel < 0) nuevoNivel = 0;
                    if (nuevoNivel > 100) nuevoNivel = 100;

                    seekBarGradient.setProgress(nuevoNivel);
                    actualizarDegradado(nuevoNivel);
                    return true;

                case MotionEvent.ACTION_UP:
                    float finalY = event.getY();
                    float deltaFinal = inicialYGradient - finalY;
                    nivelDegradado += (int) (deltaFinal / 5);

                    if (nivelDegradado < 0) nivelDegradado = 0;
                    if (nivelDegradado > 100) nivelDegradado = 100;

                    seekBarGradient.setProgress(nivelDegradado);
                    actualizarDegradado(nivelDegradado);
                    return true;
            }
            return false;
        });

        zoneExit.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    inicialXExit = event.getX();
                    return true;

                case MotionEvent.ACTION_UP:
                    if (event.getX() - inicialXExit > 100) {
                        finishAffinity();
                        return true;
                    }
                    break;
            }
            return false;
        });

        zoneClear.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    inicialXClear = event.getX();
                    return true;

                case MotionEvent.ACTION_UP:
                    if (inicialXClear - event.getX() > 100) {
                        etMonto.setText("");
                        tvResultado.setText("0");
                        return true;
                    }
                    break;
            }
            return false;
        });
    }
}