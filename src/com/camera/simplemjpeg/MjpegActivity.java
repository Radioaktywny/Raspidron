package com.camera.simplemjpeg;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.zerokol.views.JoystickView;
import com.zerokol.views.JoystickView.OnJoystickMoveListener;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import movingRasb.BluetoothControl;

public class MjpegActivity extends Activity {
	private static final boolean DEBUG = false;
	private static final String TAG = "MJPEG";
	MjpegView movieFormCamera;
	FrameLayout layout;
	RelativeLayout buttons;
	private MjpegView mv = null;
	private JoystickView joystick;
	private BluetoothControl moveRobot;
	private String lastmove = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startBT();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		View v = getLayoutInflater().inflate(R.layout.przyciski_layout, null);
		layout = new FrameLayout(this);
		movieFormCamera = new MjpegView(this);
		buttons = new RelativeLayout(this);
		mv = movieFormCamera;

		joystick = (JoystickView) findViewById(R.id.joystickView);

		buttons.addView(v);
		layout.addView(movieFormCamera);
		layout.addView(buttons);
		setContentView(layout);
		init_joistick((JoystickView) findViewById(R.id.joystickView));

		// setRequestedOrientation((ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) /
		// 2);

		// receive parameters from PreferenceActivity
		Bundle bundle = getIntent().getExtras();
		String hostname = bundle.getString(PreferenceActivity.KEY_HOSTNAME);
		String portnum = bundle.getString(PreferenceActivity.KEY_PORTNUM);
		new DoRead().execute(hostname, portnum);

	}

	private void startBT() {
		moveRobot = new BluetoothControl("");
		try {
			moveRobot.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
	}

	public void onResume() {
		if (DEBUG)
			Log.d(TAG, "onResume()");
		super.onResume();
		if (mv != null) {
			mv.resumePlayback();
		}

	}

	public void onStart() {
		if (DEBUG)
			Log.d(TAG, "onStart()");
		super.onStart();
	}

	public void onPause() {
		if (DEBUG)
			Log.d(TAG, "onPause()");
		super.onPause();
		if (mv != null) {
			mv.stopPlayback();
		}
	}

	public void onStop() {
		if (DEBUG)
			Log.d(TAG, "onStop()");
		super.onStop();
	}

	public void onDestroy() {
		if (DEBUG)
			Log.d(TAG, "onDestroy()");

		if (mv != null) {
			mv.freeCameraMemory();
		}

		super.onDestroy();
	}

	public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
		protected MjpegInputStream doInBackground(String... params) {
			Socket socket = null;
			try {
				socket = new Socket(params[0], Integer.valueOf(params[1]));
				return (new MjpegInputStream(socket.getInputStream()));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(MjpegInputStream result) {
			mv.setSource(result);
			if (result != null)
				result.setSkip(1);
			mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
			mv.showFps(true);
		}
	}

	private void init_joistick(JoystickView joystick) {
		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {
			@Override
			public void onValueChanged(int angle, int power, int direction) {
				switch (direction) {
				case JoystickView.FRONT:
					if (!lastmove.equals("LEFT")) {
						moveRobot.moveLeft();
						lastmove="LEFT";
					}
					break;
				case JoystickView.LEFT_FRONT:
					// moveRobot.moveForward();
					break;
				case JoystickView.LEFT:
					if (!lastmove.equals("FORWARD")) {
						moveRobot.moveForward();
						lastmove="FORWARD";
					}break;
				case JoystickView.BOTTOM_LEFT:
					// moveRobot.moveForward();
					break;
				case JoystickView.BOTTOM:
					if (!lastmove.equals("RIGHT")) {
						moveRobot.moveRight();
						lastmove="RIGHT";
					}break;
				case JoystickView.RIGHT_BOTTOM:
					// moveRobot.moveForward();
					break;
				case JoystickView.RIGHT:
					if (!lastmove.equals("BACK")) {
						moveRobot.moveBack();
						lastmove="BACK";
					}break;
				case JoystickView.FRONT_RIGHT:
					// moveRobot.moveForward();
					break;
				default:
				}
			}

		}, JoystickView.DEFAULT_LOOP_INTERVAL);
		joystick.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					moveRobot.dontMove();
					lastmove="STAY";
					return false;
				}
				return false;
			}
		});
	}

}