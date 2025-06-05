package com.example.helloandroid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class CalculateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        TextView currencyInfo = findViewById(R.id.currencyInfo);
        EditText inputRmb = findViewById(R.id.inputRmb);
        Button calculateButton = findViewById(R.id.calculateButton);
        TextView resultText = findViewById(R.id.resultText);

        String currencyName = getIntent().getStringExtra("currencyName");
        String currencyRate = getIntent().getStringExtra("currencyRate");

        currencyInfo.setText(currencyName + " 汇率: " + currencyRate);

        calculateButton.setOnClickListener(v -> {
            String rmbStr = inputRmb.getText().toString();
            if (rmbStr.isEmpty()) {
                Toast.makeText(CalculateActivity.this, "请输入人民币金额", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double rmb = Double.parseDouble(rmbStr);
                double rate = Double.parseDouble(currencyRate);
                double result = rmb * 100 / rate;

                DecimalFormat df = new DecimalFormat("#.##");
                resultText.setText(df.format(rmb) + " 人民币 = " + df.format(result) + " " + currencyName);
            } catch (NumberFormatException e) {
                Toast.makeText(CalculateActivity.this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
            }
        });
    }
}