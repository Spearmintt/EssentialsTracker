package com.example.essentialstracker.sample.example2_connection;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.essentialstracker.R;
import com.example.essentialstracker.RxBleConnection;
import com.example.essentialstracker.RxBleDevice;
import com.example.essentialstracker.sample.DeviceActivity;
import com.example.essentialstracker.sample.SampleApplication;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ConnectionExampleActivity extends AppCompatActivity {

    @BindView(R.id.connection_state)
    TextView connectionStateView;
    @BindView(R.id.connect_toggle)
    Button connectButton;
    @BindView(R.id.newMtu)
    EditText textMtu;
    @BindView(R.id.set_mtu)
    Button setMtuButton;
    @BindView(R.id.autoconnect)
    SwitchCompat autoConnectToggleSwitch;
    private RxBleDevice bleDevice;
    private Disposable connectionDisposable;
    private final CompositeDisposable mtuDisposable = new CompositeDisposable();
    private Disposable stateDisposable;

    @OnClick(R.id.connect_toggle)
    public void onConnectToggleClick() {
        if (isConnected()) {
            triggerDisconnect();
        } else {
            connectionDisposable = bleDevice.establishConnection(autoConnectToggleSwitch.isChecked())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(this::dispose)
                    .subscribe(this::onConnectionReceived, this::onConnectionFailure);
        }
    }

    @TargetApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    @OnClick(R.id.set_mtu)
    public void onSetMtu() {
        final Disposable disposable = bleDevice.establishConnection(false)
                .flatMapSingle(rxBleConnection -> rxBleConnection.requestMtu(72))
                .take(1) // Disconnect automatically after discovery
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::updateUI)
                .subscribe(this::onMtuReceived, this::onConnectionFailure);
        mtuDisposable.add(disposable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example2);
        ButterKnife.bind(this);
        String macAddress = getIntent().getStringExtra(DeviceActivity.EXTRA_MAC_ADDRESS);
        setTitle("MAC: {macAddress}");
        bleDevice = SampleApplication.getRxBleClient(this).getBleDevice(macAddress);
        // How to listen for connection state changes
        // Note: it is meant for UI updates only ??? one should not observeConnectionStateChanges() with BLE connection logic
        stateDisposable = bleDevice.observeConnectionStateChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnectionStateChange);
    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(android.R.id.content), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    @SuppressWarnings("unused")
    private void onConnectionReceived(RxBleConnection connection) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(android.R.id.content), "Connection received", Snackbar.LENGTH_SHORT).show();
    }

    private void onConnectionStateChange(RxBleConnection.RxBleConnectionState newState) {
        connectionStateView.setText(newState.toString());
        updateUI();
    }

    private void onMtuReceived(Integer mtu) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(android.R.id.content), "MTU received: " + mtu, Snackbar.LENGTH_SHORT).show();
    }

    private void dispose() {
        connectionDisposable = null;
        updateUI();
    }

    private void triggerDisconnect() {

        if (connectionDisposable != null) {
            connectionDisposable.dispose();
        }
    }

    private void updateUI() {
        final boolean connected = isConnected();
        connectButton.setText(connected ? "Disconnect" : "Connect");
        autoConnectToggleSwitch.setEnabled(!connected);
    }

    @Override
    protected void onPause() {
        super.onPause();

        triggerDisconnect();
        mtuDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (stateDisposable != null) {
            stateDisposable.dispose();
        }
    }
}
