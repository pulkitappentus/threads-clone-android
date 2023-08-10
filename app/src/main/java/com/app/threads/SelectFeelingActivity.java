package com.app.threads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.threads.adapter.FeelingsListAdapter;
import com.app.threads.app.App;
import com.app.threads.util.Api;
import com.app.threads.util.Helper;

public class SelectFeelingActivity extends AppCompatActivity {

    RecyclerView feelingRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_feeling);

       TextView tvHeading=findViewById(R.id.tvHeading);
        feelingRecycler=findViewById(R.id.recycler_view);
        final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);


       tvHeading.setText(R.string.howAreYouFeeling);

        final FeelingsListAdapter feelingsAdapter;

        feelingsAdapter = new FeelingsListAdapter(this, App.getInstance().getFeelingsList());

        final LinearLayoutManager mLayoutManager = new GridLayoutManager(this, Helper.getStickersGridSpanCount(this));
        feelingRecycler.setLayoutManager(mLayoutManager);
        feelingRecycler.setHasFixedSize(true);
        feelingRecycler.setItemAnimator(new DefaultItemAnimator());

        feelingRecycler.setAdapter(feelingsAdapter);

        feelingRecycler.setNestedScrollingEnabled(true);

        feelingsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {

                super.onChanged();

                if (App.getInstance().getFeelingsList().size() != 0) {

                    feelingRecycler.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        feelingsAdapter.setOnItemClickListener(new FeelingsListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {


            }
        });

        if (App.getInstance().getFeelingsList().size() == 0) {

            feelingRecycler.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);

            Api api = new Api(this);
            api.getFeelings(feelingsAdapter);
        }


    }
}