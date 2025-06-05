package com.example.helloandroid;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RateActivity extends AppCompatActivity {
    private Spinner spFrom, spTo;
    private EditText etAmount;
    private TextView tvResult;
    private double usdRate = 6.5, eurRate = 7.8, jpyRate = 0.059;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        // 初始化视图
        etAmount = findViewById(R.id.et_amount);
        spFrom = findViewById(R.id.sp_from);
        spTo = findViewById(R.id.sp_to);
        tvResult = findViewById(R.id.tv_result);

        Button btnCalculate = findViewById(R.id.btn_calculate);
        Button btnRefresh = findViewById(R.id.btn_refresh);

        // 设置点击监听器
        btnCalculate.setOnClickListener(v -> calculate());
        btnRefresh.setOnClickListener(v -> fetchRatesFromWeb());
    }

    private void calculate() {
        try {
            double amount = Double.parseDouble(etAmount.getText().toString());
            int from = spFrom.getSelectedItemPosition();
            int to = spTo.getSelectedItemPosition();

            double[] rates = {1.0, usdRate, eurRate, jpyRate};
            double result = amount * rates[from] / rates[to];
            tvResult.setText(String.format("结果: %.2f", result));
        } catch (NumberFormatException e) {
            tvResult.setText("请输入有效金额");
        }
    }

    private void fetchRatesFromWeb() {
        executor.execute(() -> {
            try {
                // 使用Jsoup获取中国银行外汇牌价页面
                Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/")
                        .timeout(10000)
                        .get();

                // 解析美元汇率
                Elements rows = doc.select("table.publish table tr");
                for (Element row : rows) {
                    if (row.text().contains("美元")) {
                        Elements cols = row.select("td");
                        // 现汇卖出价在第四列
                        String rateStr = cols.get(3).text();
                        usdRate = 1.0 / Double.parseDouble(rateStr);
                        break;
                    }
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "汇率更新成功", Toast.LENGTH_SHORT).show();
                    tvResult.setText("美元汇率已更新: " + usdRate);
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "获取汇率失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvResult.setText("使用默认汇率");
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "解析汇率失败", Toast.LENGTH_SHORT).show();
                    tvResult.setText("使用默认汇率");
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}