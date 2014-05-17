package com.google.hackathon.museum.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.google.android.gms.plus.PlusShare;
import com.google.hackathon.museum.R;
import com.google.hackathon.museum.delegate.EstimoteApiGatewayDelegate;
import com.google.hackathon.museum.delegate.EstimoteBeaconServiceDelegate;
import com.google.hackathon.museum.model.Art;
import com.google.hackathon.museum.service.EstimoteApiGateway;
import com.google.hackathon.museum.service.EstimoteBeaconService;

public class MainActivity extends Activity implements
		EstimoteBeaconServiceDelegate, EstimoteApiGatewayDelegate {
	
	private Map<String, Integer> imagePath = new HashMap<String, Integer>();

	private static final String TAG = "Hackathon";
	
	private EstimoteBeaconService estimoteBeaconService = null;
	private EstimoteApiGateway estimoteApiGateway = null;
	
	private Beacon nearbyBeacon = null;
	private Beacon lastBeacon = null;
	private boolean showingImage = false;
	private MediaPlayer mp = null;
	private ImageView statusImage = null;
	
	@Override
	public void didEnterRegion() {
		statusImage.setImageResource(R.drawable.menu);
		mp = MediaPlayer.create(this, R.raw.welcome);
		mp.start();
	}
	
	@Override
	public void didExitRegion() {
		statusImage.setImageResource(R.drawable.exit);
	}
	
	@Override
	public void didGetCloserToBeacon(Beacon beacon) {
		if(!showingImage && !beacon.equals(this.lastBeacon)) {
			this.lastBeacon = beacon;
			estimoteApiGateway.findProductByNearbyBeacon(this.lastBeacon);
		}
	}

	@Override
	public void onBeaconsDiscovered(final Region region, final List<Beacon> beacons) {
		if(!beacons.isEmpty()) {
			final Beacon beacon = beacons.iterator().next();
			
			if (!beacon.equals(this.nearbyBeacon)) {
				this.nearbyBeacon = beacon;
			}
		}
	}
	
	@Override
	public void didMuseumProductFound(Art museumProduct) {
		mp.stop();
		Log.i(TAG, "Welcome to: " + museumProduct.getImagePath());
		
		if(!showingImage) {
			LayoutInflater inflater = getLayoutInflater();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View modal = inflater.inflate(R.layout.show_image, null);
			ImageView modelImage = (ImageView)modal.findViewById(R.id.model_image);
			String image = museumProduct.getImagePath();
			modelImage.setImageResource(imagePath.get(image));
			
			if(image.equals("whale.png")) {
				mp = MediaPlayer.create(this, R.raw.pedra);
			} else {
				mp = MediaPlayer.create(this, R.raw.yoyo);
			}
			
			mp.start();
			
			builder.setView(modal)
				.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					showingImage = false;
					mp.stop();
				}
			});
			
			modelImage.setImageResource(imagePath.get(museumProduct.getImagePath()));
				
			AlertDialog dialog = builder.create();
			dialog.show();
			showingImage = true;
		}
				
	}
	
	// Lifecycle callbacks

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		this.statusImage = (ImageView) findViewById(R.id.entrance);
		
		mp = MediaPlayer.create(this, R.raw.welcome);
		try {
			mp.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.imagePath.put("whale.png", R.drawable.rock_sculpture);
		this.imagePath.put("tsubasa.png", R.drawable.maluco);

		estimoteBeaconService = EstimoteBeaconService.getInstance(getApplicationContext());
		estimoteBeaconService.startMonitoring();
		estimoteBeaconService.startRanging();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		estimoteBeaconService.connect();
		estimoteBeaconService.register(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		estimoteApiGateway = EstimoteApiGateway.getinstance();
		estimoteApiGateway.registerDelegate(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		estimoteBeaconService.stop();
		mp.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(1, 200, 0, "Share w/ Google+");
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 200: {
            	Intent shareIntent = new PlusShare.Builder(this)
                .setText("Checkout what I did @ GDG Hackathon!")
                .setType("image/png")
                .setContentDeepLinkId("testID",
                        "Look that place!",
                        "Test Description",
                        Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "whale"))
                .getIntent();
            	startActivityForResult(shareIntent, 0);
                 return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
