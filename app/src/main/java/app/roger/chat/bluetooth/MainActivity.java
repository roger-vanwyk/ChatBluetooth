package app.roger.chat.bluetooth;

import android.app.Activity;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.util.*;
import android.webkit.*;
import android.animation.*;
import android.view.animation.*;
import java.util.*;
import java.text.*;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Button;
import java.util.Timer;
import java.util.TimerTask;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.AdapterView;

public class MainActivity extends Activity {
	
	private Timer _timer = new Timer();
	
	private double variable = 0;
	
	private ArrayList<HashMap<String, Object>> all_data = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> connected_bt = new ArrayList<>();
	private ArrayList<String> bt_name = new ArrayList<>();
	private ArrayList<String> bt_address = new ArrayList<>();
	private ArrayList<String> listview_only = new ArrayList<>();
	
	private LinearLayout linear1;
	private ListView listview1;
	private LinearLayout linear3;
	private Switch switch1;
	private LinearLayout linear2;
	private TextView textview1;
	private Spinner spinner1;
	private EditText edittext1;
	private Button button1;
	
	private BluetoothConnect Bluetooth;
	private BluetoothConnect.BluetoothConnectionListener _Bluetooth_bluetooth_connection_listener;
	private TimerTask timer;
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		
		linear1 = (LinearLayout) findViewById(R.id.linear1);
		listview1 = (ListView) findViewById(R.id.listview1);
		linear3 = (LinearLayout) findViewById(R.id.linear3);
		switch1 = (Switch) findViewById(R.id.switch1);
		linear2 = (LinearLayout) findViewById(R.id.linear2);
		textview1 = (TextView) findViewById(R.id.textview1);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		edittext1 = (EditText) findViewById(R.id.edittext1);
		button1 = (Button) findViewById(R.id.button1);
		Bluetooth = new BluetoothConnect(this);
		
		switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2)  {
				final boolean _isChecked = _param2;
				if (_isChecked) {
					Bluetooth.activateBluetooth();
					Bluetooth.readyConnection(_Bluetooth_bluetooth_connection_listener, "Bluetooth");
					_Get_paired_devices();
				}
				else {
					android.bluetooth.BluetoothAdapter adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
					adapter.disable();
					Bluetooth.readyConnection(_Bluetooth_bluetooth_connection_listener, "Bluetooth");
				}
			}
		});
		
		spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				if (_position > 0) {
					Bluetooth.startConnection(_Bluetooth_bluetooth_connection_listener, bt_address.get((int)(_position)), "Bluetooth");
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> _param1) {
				
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (edittext1.getText().toString().equals("")) {
					QueryUtil.showMessage(getApplicationContext(), "enter message");
				}
				else {
					Bluetooth.sendData(_Bluetooth_bluetooth_connection_listener, edittext1.getText().toString(), "Bluetooth");
					edittext1.setText("");
				}
			}
		});
		
		_Bluetooth_bluetooth_connection_listener = new BluetoothConnect.BluetoothConnectionListener() {
			@Override
			public void onConnected(String _param1, HashMap<String, Object> _param2) {
				final String _tag = _param1;
				final HashMap<String, Object> _deviceData = _param2;
				connected_bt.clear();
				connected_bt.add(_deviceData);
				spinner1.setVisibility(View.GONE);
				textview1.setVisibility(View.VISIBLE);
				textview1.setText(connected_bt.get((int)0).get("name").toString());
			}
			
			@Override
			public void onDataReceived(String _param1, byte[] _param2, int _param3) {
				final String _tag = _param1;
				final String _data = new String(_param2, 0, _param3);
				listview_only.add(_data);
				listview1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, listview_only));
			}
			
			@Override
			public void onDataSent(String _param1, byte[] _param2) {
				final String _tag = _param1;
				final String _data = new String(_param2);
				listview_only.add(_data);
				listview1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, listview_only));
			}
			
			@Override
			public void onConnectionError(String _param1, String _param2, String _param3) {
				final String _tag = _param1;
				final String _connectionState = _param2;
				final String _errorMessage = _param3;
				QueryUtil.showMessage(getApplicationContext(), _errorMessage);
				spinner1.setVisibility(View.VISIBLE);
				textview1.setVisibility(View.GONE);
			}
			
			@Override
			public void onConnectionStopped(String _param1) {
				final String _tag = _param1;
				QueryUtil.showMessage(getApplicationContext(), _tag.concat(" stopped"));
				spinner1.setVisibility(View.VISIBLE);
				textview1.setVisibility(View.GONE);
			}
		};
	}
	private void initializeLogic() {
		bt_name.add("choose device");
		spinner1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, bt_name));
		textview1.setVisibility(View.GONE);
		if (Bluetooth.isBluetoothActivated()) {
			Bluetooth.readyConnection(_Bluetooth_bluetooth_connection_listener, "Bluetooth");
			switch1.setEnabled(true);
		}
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			
			default:
			break;
		}
	}
	
	private void _Get_paired_devices () {
		all_data.clear();
		bt_name.clear();
		bt_address.clear();
		bt_name.add("choose device");
		bt_address.add("none");
		timer = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (Bluetooth.isBluetoothActivated()) {
							Bluetooth.getPairedDevices(all_data);
							variable = 0;
							for(int _repeat42 = 0; _repeat42 < (int)(all_data.size()); _repeat42++) {
								bt_name.add(all_data.get((int)variable).get("name").toString());
								bt_address.add(all_data.get((int)variable).get("address").toString());
								variable++;
								spinner1.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, bt_name));
								((ArrayAdapter)spinner1.getAdapter()).notifyDataSetChanged();
								timer.cancel();
							}
						}
						else {
							
						}
					}
				});
			}
		};
		_timer.scheduleAtFixedRate(timer, (int)(0), (int)(2000));
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels(){
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels(){
		return getResources().getDisplayMetrics().heightPixels;
	}
	
}
