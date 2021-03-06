package com.example.essentialstracker.sample.example3_discovery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentialstracker.R;
import com.example.essentialstracker.RxBleConnection;
import com.example.essentialstracker.RxBleDevice;
import com.example.essentialstracker.sample.DeviceActivity;
import com.example.essentialstracker.sample.SampleApplication;
import com.example.essentialstracker.sample.example4_characteristic.CharacteristicOperationExampleActivity;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ServiceDiscoveryExampleActivity extends AppCompatActivity {

    @BindView(R.id.connect)
    Button connectButton;
    @BindView(R.id.scan_results)
    RecyclerView recyclerView;
    private DiscoveryResultsAdapter adapter;
    private RxBleDevice bleDevice;
    private String macAddress;
    private final CompositeDisposable servicesDisposable = new CompositeDisposable();

    @OnClick(R.id.connect)
    public void onConnectToggleClick() {
        final Disposable disposable = bleDevice.establishConnection(false)
                .flatMapSingle(RxBleConnection::discoverServices)
                .take(1) // Disconnect automatically after discovery
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::updateUI)
                .subscribe(adapter::swapScanResult, this::onConnectionFailure);
        servicesDisposable.add(disposable);

        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example3);
        ButterKnife.bind(this);
        macAddress = getIntent().getStringExtra(DeviceActivity.EXTRA_MAC_ADDRESS);
        //noinspection ConstantConditions
        getSupportActionBar().setSubtitle("MAC: {macAddress}");
        bleDevice = SampleApplication.getRxBleClient(this).getBleDevice(macAddress);
        configureResultList();
    }

    private void configureResultList() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        adapter = new DiscoveryResultsAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnAdapterItemClickListener(view -> {
            final int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
            final DiscoveryResultsAdapter.AdapterItem itemAtPosition = adapter.getItem(childAdapterPosition);
            onAdapterItemClick(itemAtPosition);
        });
    }

    private void onAdapterItemClick(DiscoveryResultsAdapter.AdapterItem item) {

        if (item.type == DiscoveryResultsAdapter.AdapterItem.CHARACTERISTIC) {
            final Intent intent = CharacteristicOperationExampleActivity.startActivityIntent(this, macAddress, item.uuid);
            // If you want to check the alternative advanced implementation comment out the line above and uncomment one below
//            final Intent intent = AdvancedCharacteristicOperationExampleActivity.startActivityIntent(this, macAddress, item.uuid);
            startActivity(intent);
        } else {
            //noinspection ConstantConditions
            Snackbar.make(findViewById(android.R.id.content), "No Action", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(android.R.id.content), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    private void updateUI() {
        connectButton.setEnabled(!isConnected());
    }

    @Override
    protected void onPause() {
        super.onPause();
        servicesDisposable.clear();
    }
}
