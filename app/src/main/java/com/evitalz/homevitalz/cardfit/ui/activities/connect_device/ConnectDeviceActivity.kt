 package com.evitalz.homevitalz.cardfit.ui.activities.connect_device

import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.HandlerDeviceConnect
import com.evitalz.homevitalz.cardfit.PairHandler
import com.evitalz.homevitalz.cardfit.R
import com.evitalz.homevitalz.cardfit.Utility
import com.evitalz.homevitalz.cardfit.databinding.ActivityConnectDeviceBinding
import com.evitalz.homevitalz.cardfit.databinding.ConnectDialogBinding
import com.evitalz.homevitalz.cardfit.ui.activities.spo2.DataReceiverSpo2Activity
import com.evitalz.homevitalz.cardfit.ui.model.MyDeviceModel
import com.evitalz.homevitalz.cardfit.ui.viewmodels.ConnectDeviceViewModel

 class ConnectDeviceActivity : AppCompatActivity(), HandlerDeviceConnect, PairHandler {

    lateinit var binding: ActivityConnectDeviceBinding
    lateinit var dialogBinding: ConnectDialogBinding
    lateinit var BA: BluetoothAdapter
    lateinit var dialog: Dialog

    companion object{
        const val LEFTHAND = 100
        const val LEFTWRIST = 101
        const val LEFTLEG = 102
        const val CHEST = 103
        const val ROW_ID = "row_id"
        const val TAG = "result_test"
        const val RESULT_NEW_DEVICE = 1000
    }
    lateinit var adapter: PairDeviceAdapter
    private val viewmodel: ConnectDeviceViewModel by lazy {
        ViewModelProvider(this).get(
            ConnectDeviceViewModel::class.java
        ) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler=this

        BA = BluetoothAdapter.getDefaultAdapter()

        if (!BA.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1001)
        }
//        viewmodel.deviceavailableListLive.observe(this, Observer {
//            adapter.devicelist = it
//            adapter.notifyDataSetChanged()
//            Log.d("search_test", "onCreate: ${it.size}")
//        })
        val name = Utility.getSavedBluetoothAddress(this)
        val paireddevices=BA.bondedDevices
        if(paireddevices.size>0){
            for(device in paireddevices){
                val deviceName = device.name
                if(device.address == name){
                    Log.d("paireddevices", "onCreate:${device.address} ")
                    viewmodel.currentDevice = device
//                    viewmodel.addavailableDevice(MyDeviceModel(deviceName, device))
//                    binding.devicemodel=MyDeviceModel(deviceName, device)
                    binding.let {
                        it.name = deviceName
                        it.address = device.address
                        it.executePendingBindings()
                        if(it.name.equals("TNG SPO2")){
                            binding.ivecg.setImageResource(R.drawable.tng_spo2)
                        }else{
                            binding.ivecg.setImageResource(R.drawable.pm10)
                        }
                    }

                    binding.executePendingBindings()
                }
            }
            Log.d("paireddevices", "onCreate:${paireddevices.size} ")
        }


    }

    override fun onResume() {
        super.onResume()
        val paireddevices=BA.bondedDevices

//        if(paireddevices.size>0){
//            for(device in paireddevices){
//                val deviceName = device.name
//                if(deviceName.substring(0, 4).equals("PM10")){
//                    Log.d("paireddevices", "onCreate:${device.address} ")
//                    viewmodel.addavailableDevice(MyDeviceModel(deviceName, device))
//                    binding.devicemodel=MyDeviceModel(deviceName, device)
//                    binding.executePendingBindings()
//                }
//            }
//            Log.d("paireddevices", "onCreate:${paireddevices.size} ")
//        }

    }

    override fun onAddDeviceClicked(view: View) {
        startActivityForResult(Intent(this, SearchDeviceActivity::class.java)  , RESULT_NEW_DEVICE )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_NEW_DEVICE && resultCode == RESULT_OK){
            if(data != null){
                val deviceMac = data.getStringExtra(Utility.DEVICE_MAC)
                val deviceName = data.getStringExtra(Utility.DEVICE_NAME)
                binding.let {
                    it.name = deviceName
                    it.address = deviceMac
                    if(it.name == "TNG SPO2"){
                        binding.ivecg.setImageResource(R.drawable.tng_spo2)
                    }else{
                        binding.ivecg.setImageResource(R.drawable.pm10)
                    }
                    it.executePendingBindings()
                }
            }
        }
    }

    override fun onConnectDevice(model: MyDeviceModel) {

    }

    override fun onConnectClicked(view: View) {
        when(binding.tvdevname.text.toString()){
            "TNG SPO2"->{
                startActivity(Intent(this, DataReceiverSpo2Activity::class.java))
            }else->{
               startActivity(Intent(this, DataReceiverActivity::class.java))
            }
        }


    }

    override fun onBackPressed() {
        finish()
    }

    override fun onPairClicked(model: MyDeviceModel) {

    }

}