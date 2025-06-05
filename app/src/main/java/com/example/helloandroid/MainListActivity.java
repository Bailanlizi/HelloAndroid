package com.example.helloandroid;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainListActivity extends AppCompatActivity {
    private static final String TAG = "MainListActivity";
    private static final String URL = "https://www.huiivbiao.com/bank/spdb";

    private ListView listView;
    private ProgressBar progressBar;
    private Button btnFetch;
    private RateAdapter adapter;
    private List<RateItem> rateItems = new ArrayList<>();

    // 将RateItem作为内部静态类
    private static class RateItem {
        private final String currency;
        private final String rate;

        public RateItem(String currency, String rate) {
            this.currency = currency;
            this.rate = rate;
        }

        public String getCurrency() {
            return currency;
        }

        public String getRate() {
            return rate;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        btnFetch = findViewById(R.id.btnFetch);

        adapter = new RateAdapter();
        listView.setAdapter(adapter);

        btnFetch.setOnClickListener(v -> new FetchRatesTask().execute());
    }

    private class RateAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return rateItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rateItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainListActivity.this)
                        .inflate(R.layout.activity_main_list, parent, false);

                View itemLayout = convertView.findViewById(R.id.list_item_layout);
                ((ViewGroup)itemLayout.getParent()).removeView(itemLayout);
                convertView = itemLayout;
            }

            RateItem item = rateItems.get(position);
            TextView tvCurrency = convertView.findViewById(R.id.tvCurrency);
            TextView tvRate = convertView.findViewById(R.id.tvRate);

            tvCurrency.setText(item.getCurrency());
            tvRate.setText(item.getRate());

            return convertView;
        }
    }

    private class FetchRatesTask extends AsyncTask<Void, Void, List<RateItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            btnFetch.setEnabled(false);
        }

        @Override
        protected List<RateItem> doInBackground(Void... voids) {
            List<RateItem> items = new ArrayList<>();
            try {
                Document doc = Jsoup.connect(URL).get();
                Elements rows = doc.select("table.rate-table tbody tr");

                Log.d(TAG, "找到 " + rows.size() + " 行数据");

                for (Element row : rows) {
                    Elements cols = row.select("td");
                    if (cols.size() >= 2) {
                        String currency = cols.get(0).text();
                        String rate = cols.get(1).text();
                        items.add(new RateItem(currency, rate));
                        Log.d(TAG, "解析到: " + currency + " - " + rate);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "解析网页出错", e);
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<RateItem> result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            btnFetch.setEnabled(true);

            rateItems.clear();
            rateItems.addAll(result);
            adapter.notifyDataSetChanged();
        }
    }
}