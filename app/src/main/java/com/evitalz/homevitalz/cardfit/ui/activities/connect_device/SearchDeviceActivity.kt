package com.evitalz.homevitalz.cardfit.ui.activities.connect_device

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.*
import android.bluetooth.BluetoothDevice.ERROR
import android.bluetooth.BluetoothDevice.EXTRA_BOND_STATE
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.evitalz.homevitalz.cardfit.PairHandler
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.databinding.ActivitySearchDeviceBinding
import com.evitalz.homevitalz.cardfit.databinding.ConnectDialogBinding
import com.evitalz.homevitalz.cardfit.ui.activities.SplashActivity
import com.evitalz.homevitalz.cardfit.ui.model.MyDeviceModel
import com.evitalz.homevitalz.cardfit.ui.viewmodels.SearchViewModel

class SearchDeviceActivity : AppCompatActivity(), PairHandler {

    lateinit var binding: ActivitySearchDeviceBinding
    lateinit var BA: BluetoothAdapter
    lateinit var adapter: DeviceAdapter
    private val viewmodel: SearchViewModel by lazy {
        ViewModelProvider(this).get(
            SearchViewModel::class.java
        )
    }
    lateinit var dialog: Dialog
    lateinit var dialogBinding: ConnectDialogBinding

    val preferences: SharedPreferences by lazy {
        getSharedPreferences(SplashActivity.PREF, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BA = BluetoothAdapter.getDefaultAdapter()

        if (!BA.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1001)
        }

        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                REQUEST_ALL_PERMISSIONS
            )
        } else {
            displayLocationSettingsRequest(this)
        }

        adapter = DeviceAdapter(this)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

//        showMessageDialog()
        viewmodel.deviceavailableListLive.observe(this, Observer {
            adapter.devicelist = it
            adapter.notifyDataSetChanged()
            Log.d("search_test", "onCreate: ${it.size}")
        })

        BA.startDiscovery()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            mReceiver2,
            IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            }
        )
    }

    private val mReceiver2: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (BluetoothDevice.ACTION_FOUND == intent.action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    Log.d("search_test", "onReceive:${device.address} ${device.name}")
                }
                Log.d("search_test", "found")

                if (device!!.name != null) {
                    if (device.name != null && (device.name.length > 3) && device.name.substring(
                            0,
                            4
                        ) == "PM10" || device.name == "TNG SPO2" && !viewmodel.isduplicate(device)
                    ) {
                        val deviceName = device.name.trim { it <= ' ' }
                        Log.d("search_test", "onReceive: yes")
//                    dialog.dismiss()
                        viewmodel.addavailableDevice(MyDeviceModel(deviceName, device))
                    }
                }

            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == intent.action) {
                val bondState = intent.getIntExtra(EXTRA_BOND_STATE, ERROR)
                Log.d("search_test", "bond changed $bondState")
                val previousBondState =
                    intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1)
                Log.d("search_test", "bond changed $previousBondState")
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                when (bondState) {
                    BluetoothDevice.BOND_BONDED -> {
                        if (device != null) {
                            saveDevice(device)
                        }
                    }
                }
            }
        }
    }


    companion object{
        val REQUEST_ALL_PERMISSIONS = 100

        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val LOCATION_PERMISSION_REQUEST = 101

        fun hasPermissions(
            context: Context?,
            vararg permissions: String?
        ): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission!!
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }


        fun displayLocationSettingsRequest(context: Activity) {
            val mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000.toLong())
                .setFastestInterval(1 * 1000.toLong())
            val settingsBuilder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
            settingsBuilder.setAlwaysShow(true)
            val result =
                LocationServices.getSettingsClient(context)
                    .checkLocationSettings(settingsBuilder.build())
            result.addOnCompleteListener { task ->
                try {
                    val response = task.getResult(
                        ApiException::class.java
                    )
                } catch (ex: ApiException) {
                    when (ex.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            val resolvableApiException =
                                ex as ResolvableApiException
                            resolvableApiException
                                .startResolutionForResult(
                                    context,
                                    LOCATION_PERMISSION_REQUEST
                                )
                        } catch (e: IntentSender.SendIntentException) {
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        BA.cancelDiscovery()
        unregisterReceiver(mReceiver2)
    }

    fun saveDevice(device: BluetoothDevice) {
        Utility.saveSavedBluetoothAddress(device.address, this)
        setResult(RESULT_OK, Intent().apply {
            putExtra(Utility.DEVICE_NAME, device.name)
            putExtra(Utility.DEVICE_MAC, device.address)
        })
        finish()
    }

    override fun onPairClicked(model: MyDeviceModel) {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage("Are you sure you want to continue with" + " " + model.devicename + " " + "device?")
            .setPositiveButton("Yes") { _, _ ->
                if (model.devicemac.bondState == BluetoothDevice.BOND_BONDED) {
                    saveDevice(model.devicemac)
                } else {
                    model.devicemac.createBond()
                }
            }
            .setNegativeButton("No") { _, _ ->
                finish()
            }
            .show()

    }

    private fun showMessageDialog() {
        dialog = Dialog(this@SearchDeviceActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialogBinding = ConnectDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        val display = windowManager.defaultDisplay
        dialog.window?.setLayout(
            (display.width * 0.95).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }


}