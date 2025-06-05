package com.example.helloandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CurrencyAdapter adapter;
    private final List<Currency> currencyList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CurrencyAdapter(currencyList, currency -> {
            Intent intent = new Intent(CustomListActivity.this, CalculateActivity.class);
            intent.putExtra("currencyName", currency.getName());
            intent.putExtra("currencyRate", currency.getRate());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        fetchExchangeRates();
    }

    private void fetchExchangeRates() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/")
                        .timeout(10000)
                        .get();

                Elements tables = doc.select("table");
                if (!tables.isEmpty()) {
                    Element rateTable = tables.size() > 1 ? tables.get(1) : tables.first();
                    Elements rows = rateTable.select("tr");
                    List<Currency> tempList = new ArrayList<>();

                    for (int i = 1; i < rows.size(); i++) {
                        Element row = rows.get(i);
                        Elements cols = row.select("td");

                        if (cols.size() >= 6) {
                            String name = cols.get(0).text().trim();
                            String rate = cols.get(5).text().trim();

                            if (!name.isEmpty() && !rate.isEmpty()) {
                                tempList.add(new Currency(name, rate));
                            }
                        }
                    }

                    runOnUiThread(() -> {
                        currencyList.clear();
                        currencyList.addAll(tempList);
                        filterCurrencies();
                        adapter.notifyDataSetChanged();
                    });
                }
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "获取汇率失败", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void filterCurrencies() {
        List<Currency> filteredList = new ArrayList<>();
        String[] targetCurrencies = {
                "阿联酋迪拉姆", "澳大利亚元", "巴西里亚尔", "加拿大元",
                "瑞士法郎", "丹麦克朗", "欧元", "英镑",
                "港币", "印尼卢比", "印度卢比", "日元",
                "韩国元", "澳门元"
        };

        for (Currency currency : currencyList) {
            for (String target : targetCurrencies) {
                if (currency.getName().contains(target)) {
                    filteredList.add(currency);
                    break;
                }
            }
        }

        currencyList.clear();
        currencyList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }
}