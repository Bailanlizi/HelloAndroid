package com.example.helloandroid;// CurrencyExchangeActivity.java
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloandroid.R;

import java.util.Date;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrencyExchangeActivity extends AppCompatActivity {
    private static final String BASE_CURRENCY = "USD";
    private static final String TARGET_CURRENCY = "EUR";

    private ExchangeRateDbHelper dbHelper;
    private TextView rateTextView;
    private TextView lastUpdateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_exchange);

        rateTextView = findViewById(R.id.rate_text_view);
        lastUpdateTextView = findViewById(R.id.last_update_text_view);
        dbHelper = new ExchangeRateDbHelper(this);

        checkAndFetchExchangeRate();
    }

    private void checkAndFetchExchangeRate() {
        ExchangeRate existingRate = dbHelper.getRate(BASE_CURRENCY, TARGET_CURRENCY);

        if (existingRate != null && existingRate.isUpdatedToday()) {
            showExchangeRate(existingRate, "数据库");
        } else {
            fetchExchangeRateFromNetwork();
        }
    }

    private void fetchExchangeRateFromNetwork() {
        rateTextView.setText("正在获取最新汇率...");

        ExchangeRateApiService apiService = RetrofitClient.getApiService();
        apiService.getLatestRates(BASE_CURRENCY).enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Double rate = response.body().rates.get(TARGET_CURRENCY);
                    if (rate != null) {
                        ExchangeRate newRate = new ExchangeRate();
                        newRate.baseCurrency = BASE_CURRENCY;
                        newRate.targetCurrency = TARGET_CURRENCY;
                        newRate.rate = rate;
                        newRate.lastUpdateDate = new Date();

                        dbHelper.insertOrUpdateRate(BASE_CURRENCY, TARGET_CURRENCY, rate);
                        showExchangeRate(newRate, "网络");
                    }
                } else {
                    showError("获取汇率失败");
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                showError("网络错误: " + t.getMessage());
            }
        });
    }

    private void showExchangeRate(ExchangeRate rate, String source) {
        String rateText = String.format("1 %s = %.4f %s",
                rate.baseCurrency, rate.rate, rate.targetCurrency);
        String updateText = String.format("最后更新: %tF %tT (%s)",
                rate.lastUpdateDate, rate.lastUpdateDate, source);

        rateTextView.setText(rateText);
        lastUpdateTextView.setText(updateText);
    }

    private void showError(String message) {
        rateTextView.setText(message);
        lastUpdateTextView.setText("尝试从数据库获取...");

        // 尝试显示数据库中的旧数据
        ExchangeRate existingRate = dbHelper.getRate(BASE_CURRENCY, TARGET_CURRENCY);
        if (existingRate != null) {
            showExchangeRate(existingRate, "数据库(可能过期)");
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}