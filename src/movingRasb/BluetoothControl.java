package movingRasb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothControl {
	protected static String MAC = "B8:27:EB:8D:1A:37";// BF:C1:B0";
	private BluetoothSocket mmSocket;
	private final String OWNER = "Maciek";
	private final BluetoothDevice mmDevice;
	protected PrintWriter out;
	protected BufferedReader bReader;
	public static boolean connected;

	public BluetoothControl(String MAC) {
		MAC = OWNER.equals("Maciek") ? "B8:27:EB:8D:1A:37" : "B8:27:EB:BF:C1:B0";
		// this.MAC=MAC;
		BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
		mmDevice = ba.getRemoteDevice(MAC);

		BluetoothSocket tmp = null;
		try {
			String uid = "00001101-0000-1000-8000-00805F9B34FB";
			UUID uuid = UUID.fromString(uid);
			tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mmSocket = tmp;

		// TODO Auto-generated constructor stub
	}

	public boolean isConnected() {
		return connected;
	}

	public BluetoothSocket connect() throws IOException {
		BluetoothAdapter.getDefaultAdapter();
		mmSocket.connect();
		connected = true;
		out = new PrintWriter(mmSocket.getOutputStream(), true);
		bReader = new BufferedReader(new InputStreamReader(mmSocket.getInputStream()));
		return mmSocket;
	}

	public void disconnect() throws IOException {
		mmSocket.close();
		connected = false;
	}

	public void moveRight() {
		if (out != null) {
			out.println("RIGHT");
			out.flush();
		}
	}

	public void moveLeft() {
		if (out != null) {
			out.println("LEFT");
			out.flush();
		}
	}

	public void moveForward() {
		if (out != null) {
			out.println("FORWARD");
			out.flush();
		}
	}

	public void moveBack() {
		if (out != null) {
			out.println("BACK");
			out.flush();
		}
	}

	public void dontMove() {
		if (out != null) {
			out.println("STAY");
			out.flush();
		}
		// TODO Auto-generated method stub
		
	}
}
