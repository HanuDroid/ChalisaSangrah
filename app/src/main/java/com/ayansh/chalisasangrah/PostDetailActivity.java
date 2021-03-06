package com.ayansh.chalisasangrah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class PostDetailActivity extends FragmentActivity implements
		PostDetailFragment.Callbacks {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		MobileAds.initialize(this, "ca-app-pub-4571712644338430~8332778304");

		// Hide some views
		View postList = findViewById(R.id.post_list);
		if(postList != null){
			postList.setVisibility(View.GONE);
		}

		Bundle extras = new Bundle();
		extras.putString("max_ad_content_rating", "G");

		// Show Ad.
		AdRequest adRequest = new AdRequest.Builder()
				.addNetworkExtrasBundle(AdMobAdapter.class, extras)
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("9F11CAC92EB404500CAA3F8B0BBA5277").build();
		AdView adView = (AdView) findViewById(R.id.adView);

		// Start loading the ad in the background.
		adView.loadAd(adRequest);

		Intent intent = getIntent();
		int postId = intent.getIntExtra("PostId", 0);

		// Create the Fragment.
		FragmentManager fm = this.getSupportFragmentManager();

		// Create Post List Fragment
		Fragment fragment = new PostDetailFragment();
		Bundle arguments = new Bundle();
		arguments.putInt("PostId", postId);
		fragment.setArguments(arguments);

		fm.beginTransaction().replace(R.id.post_detail, fragment)
				.commitAllowingStateLoss();

	}

	@Override
	public void loadPostsByCategory(String taxonomy, String name) {
		// Nothing to do
	}

	@Override
	public boolean isDualPane() {
		return false;
	}
}