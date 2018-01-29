package com.example.carson.yikeapp.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.carson.yikeapp.R;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Spinner spinner;
    private String[] genders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);

        initViews();
        initEvents();

        Intent intent = getIntent();
        String token = intent.getStringExtra("token");

        Toast.makeText(getApplicationContext(), "token is " + token,
                Toast.LENGTH_LONG).show();

    }

    private void initEvents() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.finish();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DetailActivity.this, genders[position],
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initViews() {
        spinner = findViewById(R.id.spinner_gender);
        genders = getResources().getStringArray(R.array.spinner_gender);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(DetailActivity.this,
                R.layout.spinner_style, genders);
        genderAdapter.setDropDownViewResource(R.layout.spinner_style_text);
        spinner.setAdapter(genderAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
