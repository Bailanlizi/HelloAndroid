package com.example.helloandroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> list1=new ArrayList<String>();
        for(int i=10;i<20;i++){
            list1.add("item"+i);
        }
        ListAdapter adapter=new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1,list1);
        setListAdapter(adapter);
    }
}