package com.ayansh.chalisasangrah;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.ayansh.hanudroid.Application;
import com.ayansh.hanudroid.HanuFragmentInterface;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class Main extends FragmentActivity implements PostListFragment.Callbacks,
												PostDetailFragment.Callbacks {

	private boolean dualPane;
	private Application app;
	private HanuFragmentInterface fragmentUI;
	private int postId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		MobileAds.initialize(this, "ca-app-pub-4571712644338430~8332778304");

        if(savedInstanceState != null){
        	postId = savedInstanceState.getInt("PostId");
        }
        else{
        	postId = 0;
        }
        
        if (findViewById(R.id.post_list) != null) {
            dualPane = true;
        }
		else{
			dualPane = false;
		}

        // Get Application Instance.
        app = Application.getApplicationInstance();
        
		// Start the Main Activity
        startMainScreen();
    }

	private void startMainScreen() {

		// Show Ad.
		Bundle extras = new Bundle();
		extras.putString("max_ad_content_rating", "G");

		AdRequest adRequest = new AdRequest.Builder()
				.addNetworkExtrasBundle(AdMobAdapter.class, extras)
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("9F11CAC92EB404500CAA3F8B0BBA5277").build();

		AdView adView = (AdView) findViewById(R.id.adView);

		// Start loading the ad in the background.
		adView.loadAd(adRequest);

		// Load Posts.
		Application.getApplicationInstance().getAllPosts();

		// Create the Fragment.
		FragmentManager fm = this.getSupportFragmentManager();
		Fragment fragment;
		
		// Create Post List Fragment
		fragment = new PostListFragment();
		Bundle arguments = new Bundle();
		arguments.putInt("PostId", postId);
		arguments.putBoolean("DualPane", dualPane);
		fragment.setArguments(arguments);
				
		if(dualPane){
			arguments.putBoolean("ShowFirstItem", true);
			fm.beginTransaction().replace(R.id.post_list, fragment).commitAllowingStateLoss();
		}
		else{
			arguments.putBoolean("ShowFirstItem", false);
			fm.beginTransaction().replace(R.id.post_detail, fragment).commitAllowingStateLoss();
		}

		fragmentUI = (HanuFragmentInterface) fragment;
		
	}

	private void showHelp() {
		// Show Help
		Intent help = new Intent(Main.this, DisplayFile.class);
		help.putExtra("File", "help.html");
		help.putExtra("Title", "Help: ");
		Main.this.startActivity(help);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	@Override
	public void onItemSelected(int id) {
		
		if (dualPane) {
            Bundle arguments = new Bundle();
            arguments.putInt("PostId", id);
            PostDetailFragment fragment = new PostDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.post_detail, fragment)
                    .commit();

        }
		else{
			Intent postDetail = new Intent(Main.this, PostDetailActivity.class);
			postDetail.putExtra("PostId", id);
			Main.this.startActivity(postDetail);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(fragmentUI != null){
			outState.putInt("PostId", fragmentUI.getSelectedItem());
		}
	}
	
	@Override
	protected void onDestroy(){
		app.close();
		super.onDestroy();
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()){
    	
    	case R.id.Help:
    		showHelp();
    		break;
    		
    	case R.id.About:
    		Intent info = new Intent(Main.this, DisplayFile.class);
			info.putExtra("File", "about.html");
			info.putExtra("Title", "About: ");
			Main.this.startActivity(info);
    		break;
    	
    	}
    	
    	return true;
    }
        
	@Override
	public void loadPostsByCategory(String taxonomy, String name) {
		
		if(taxonomy.contentEquals("category")){
			app.getPostsByCategory(name);
		}
		else if(taxonomy.contentEquals("post_tag")){
			app.getPostsByTag(name);
		}
		else if(taxonomy.contentEquals("author")){
			app.getPostsByAuthor(name);
		}
		
		this.runOnUiThread(new Runnable() {
		    public void run(){
		    	fragmentUI.reloadUI();
		    }
		});
	}
	
	@Override
	public boolean isDualPane(){
		return dualPane;
	}

}