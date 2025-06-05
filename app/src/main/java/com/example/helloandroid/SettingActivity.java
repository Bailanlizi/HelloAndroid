package com.example.helloandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {
    private EditText etUsd, etEur, etJpy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        etUsd = findViewById(R.id.et_usd);
        etEur = findViewById(R.id.et_eur);
        etJpy = findViewById(R.id.et_jpy);
        Button btnSave = findViewById(R.id.btn_save);

        // 接收当前汇率
        Intent intent = getIntent();
        etUsd.setText(String.valueOf(intent.getDoubleExtra("USD", 6.5)));
        etEur.setText(String.valueOf(intent.getDoubleExtra("EUR", 7.8)));
        etJpy.setText(String.valueOf(intent.getDoubleExtra("JPY", 0.059)));

        btnSave.setOnClickListener(v -> saveRates());
    }

    private void saveRates() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("NEW_USD", parseDouble(etUsd.getText().toString()));
        resultIntent.putExtra("NEW_EUR", parseDouble(etEur.getText().toString()));
        resultIntent.putExtra("NEW_JPY", parseDouble(etJpy.getText().toString()));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private double parseDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}