package com.google.ar.sceneform.samples;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.ar.sceneform.samples.DB.Detail_Statistics_Fragment;
import com.google.ar.sceneform.samples.DB.Total_Statistics_Fragment;
import com.google.ar.sceneform.samples.augmentedimage.R;

public class DBActivity extends AppCompatActivity {

	private Fragment shownFragment;
	public Total_Statistics_Fragment total_statistics_fragment;
	public Detail_Statistics_Fragment detail_statistics_fragment;
	public TextView tvNothing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_db);

		SetToolbar(getString(R.string.total_statistics));
		SetToolbarBack();

		tvNothing = findViewById(R.id.tvViewStatu);

		if(savedInstanceState == null) {
			total_statistics_fragment=Total_Statistics_Fragment.newInstance();
			ShowFragment(total_statistics_fragment);
		}
	}

	void BackButton() {
		if(shownFragment.equals(total_statistics_fragment)) {
			finish();
		} else {
			SetToolbar(getString(R.string.tbTitleTotal));
			ShowFragment(total_statistics_fragment);
		}
		tvNothing.setVisibility(View.INVISIBLE);
	}
	@Override
	public void onBackPressed() {
		BackButton();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				BackButton();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void SetToolbar(@NonNull String title) {
		Toolbar toolbar = findViewById(R.id.statistic_toolbar);
		toolbar.setTitle(title);
		setSupportActionBar(toolbar);
	}

	public void SetToolbarBack() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	public void ShowFragment(final Fragment fragment) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.fragmentHolder, fragment);
		fragmentTransaction.commitNow();
		shownFragment = fragment;
	}
}
