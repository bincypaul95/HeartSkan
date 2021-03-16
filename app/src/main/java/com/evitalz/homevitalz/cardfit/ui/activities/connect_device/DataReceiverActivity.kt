package com.evitalz.homevitalz.cardfit.ui.activities.connect_device

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.*
import com.evitalz.homevitalz.cardfit.database.Device_Readings
import com.evitalz.homevitalz.cardfit.databinding.*
import com.evitalz.homevitalz.cardfit.pm10_sdk.BluetoothChatService
import com.evitalz.homevitalz.cardfit.pm10_sdk.BluetoothChatService.*
import com.evitalz.homevitalz.cardfit.pm10_sdk.CallBack
import com.evitalz.homevitalz.cardfit.pm10_sdk.ICallBack
import com.evitalz.homevitalz.cardfit.pm10_sdk.MtBuf
import com.evitalz.homevitalz.cardfit.ui.viewmodels.DataReceiverViewmodel
import kotlinx.android.synthetic.main.deviceplacement.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class DataReceiverActivity : AppCompatActivity(), ICallBack, DialogHandler, DialogRetryHandler,
    DialogDevicePlacementHandler {

    val TAG="bluetooth_test"
    var dialogBinding: ConnectDialogBinding? = null
    var showretrydialogBinding: ShowretrydialogBinding?=null
    lateinit var confirmdataDialogBinding: ConfirmdataDialogBinding
    lateinit var binding: ActivityDataReceiverBinding
    lateinit var adapter: BluetoothAdapter
    var chatService  : BluetoothChatService? = null
    lateinit var dialog:Dialog
    lateinit var retrydialog: Dialog
    lateinit var case:String
    lateinit var heartrate :String
    lateinit var ecgvalues :String
    lateinit var deviceplacement: String
    lateinit var dialogPlacement: Dialog
    var isDataReceived :Boolean = false
    lateinit var deviceplacementBinding: DeviceplacementBinding
    var selectedplacement : String=""
    lateinit var alertDialog: AlertDialog
    var list= ArrayList<String>()



    private val viewmodel : DataReceiverViewmodel by lazy {
       ViewModelProvider(this).get(DataReceiverViewmodel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityDataReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler=this

        showDevicePlacementDialog()
        adapter = BluetoothAdapter.getDefaultAdapter()

    }

    private fun makeGattUpdateIntentFilter(): IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_GATT_CONNECTED)
        intentFilter.addAction(ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(ACTION_DATA_AVAILABLE)
        intentFilter.addAction(ACTION_DATA_NOT_AVAILABLE)
        intentFilter.addAction(ACTION_GATT_DEVICE_NOT_FOUND)
        intentFilter.addAction("Data_Received")
        intentFilter.addAction("Receiving_Data")
        return intentFilter
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, makeGattUpdateIntentFilter())
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

     val receiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(p0: Context?, intent: Intent?) {
            Log.d(TAG, "onreceive => ${intent?.action}")
            when(intent?.action){

                "Receiving_Data" -> {
                    Log.d(TAG, "receiving_data broadcast")
                    chatService?.stop()
                    chatService = null
                    System.gc()
                    val date_time = intent.getStringExtra("DATE")
                    val type = intent.getStringExtra("TYPE")
                    val manuf = intent.getStringExtra("MANUF")
                    case = intent.getStringExtra("VAL1").toString()
                    heartrate = intent.getStringExtra("VAL2").toString()
                    ecgvalues = intent.getStringExtra("ECG_VALUE").toString()

                    isDataReceived = true
                    dialogBinding?.textmsg?.text = "Data Received"
                    dialog.dismiss()
                    dialog.cancel()
                    binding.datastorelayout.visibility = View.VISIBLE
                    binding.tvdatetime.text =
                            Utility.simpleDateFormat.format(viewmodel.datetime.time.time)
                    binding.tvcase.text = case
                    binding.tvhrv.text = heartrate + "bpm"
                    binding.tvplacementtype.text = selectedplacement

                    val deviceReadings =
                            Device_Readings(
                                    0, 1, 1,
                                    case!!,
                                    heartrate!!,
                                    selectedplacement, "", Calendar.getInstance().timeInMillis, 0, "ECG",5, 1, "",
                                    ecgvalues, "",
                                 Calendar.getInstance().timeInMillis
                            )

                     viewmodel.insertdevicereadings(deviceReadings,selectedplacement)


                }
                "Data_Received" -> {

                }
                ACTION_GATT_CONNECTED ->{
                    dialogBinding?.textmsg?.text = "Connected"

                }
                ACTION_GATT_DEVICE_NOT_FOUND -> {
                    showRetryDialog(1)
                }

                ACTION_GATT_DISCONNECTED -> {

                    if (!isDataReceived) {
                        showRetryDialog(2)
                    }
//                    else{
//                        dialog.show()
//                        dialogBinding.textmsg.text = "Disconnected"
//                        dialogBinding.avi.visibility = View.GONE
//                    }
                    chatService?.stop()
                    System.gc()

                }
                ACTION_DATA_AVAILABLE -> {
                    dialogBinding?.textmsg?.text = resources.getString(R.string.Checkingdatafromdevice)
                }
                ACTION_DATA_NOT_AVAILABLE -> {
                    chatService?.stop()
                    isDataReceived = true
                    if(dialogBinding != null) {
                        dialogBinding!!.textmsg.text = resources.getString(R.string.NodataAvailable)
                        dialogBinding!!.avi.visibility = View.GONE
                        dialogBinding!!.cancel.visibility = View.GONE
                        dialogBinding!!.btnok.visibility = View.VISIBLE
                        dialogBinding!!.btnok.setOnClickListener {
                            dialog.dismiss()
                            finish()
                        }
                    }
                }
                ACTION_CHECKING_DATA ->
                    dialogBinding?.textmsg?.text =
                            resources.getString(R.string.Checkingdatafromdevice)

                ACTION_SETTING_DEVICE_DATE ->
                    dialogBinding?.textmsg?.text = resources.getString(R.string.SettingDeviceDate)

                ACTION_CLEARING_DATA ->
                    dialogBinding?.textmsg?.text = resources.getString(R.string.ClearingDeviceData)

            }
        }

    }

    override fun call() {
        val _ver = MtBuf.m_buf
        for (i in _ver.indices) {
            Log.i("............", Integer.toHexString(_ver[i] and 0xFF))
        }
    }

    override fun onSearchCancelClicked(view: View) {
            chatService?.stop()
        dialog.dismiss()
        finish()
    }

    override fun onOkClicked(view: View) {
        dialog.dismiss()
        finish()
    }

    override fun onDataSaveClicked(view: View) {

        Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_LONG).show()


        alertDialog = AlertDialog.Builder(this)
                .setMessage("Do you want to take test again?")
                .setPositiveButton("YES") { _, _->
                    binding.datastorelayout.visibility=View.GONE
                    list.add(selectedplacement)
                    showDevicePlacementDialog()
                }
                .setNegativeButton("NO") { _, _ ->
                    ShowECGGraphActivity()
                }
                .create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    override fun onRetryClicked(view: View) {

        retrydialog.dismiss()
        showMessageDialog()
        GlobalScope.launch {
            chatService?.stop()
            delay(2000L)
            createService()
            chatService!!.start()
            val device = adapter.getRemoteDevice(Utility.getSavedBluetoothAddress(this@DataReceiverActivity))
            chatService!!.connect(device)}

    }

    override fun onResetClicked(view: View) {
        resetbluetooth()
    }

    override fun onCancelClicked(view: View) {
        retrydialog.dismiss()
        finish()
    }

    private fun showMessageDialog(){
        dialog = Dialog(this@DataReceiverActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialogBinding= ConnectDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding!!.root)
        val display = windowManager.defaultDisplay
        dialog.window?.setLayout(
                (display.width * 0.95).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    private fun showDevicePlacementDialog() {
        dialogPlacement = Dialog(this@DataReceiverActivity)
        dialogPlacement.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogPlacement.setCancelable(false)
        deviceplacementBinding= DeviceplacementBinding.inflate(layoutInflater)
        dialogPlacement.setContentView(deviceplacementBinding.root)
        deviceplacementBinding.handler=this
        selectedplacement=""
        val display = windowManager.defaultDisplay
        dialogPlacement.window?.setLayout((display.width * 0.95).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        dialogPlacement.show()

            for (i in list ){
                when(i){
                    "Left Hand" -> {
                        dialogPlacement.ivlefthand.alpha =0.3f
                        dialogPlacement.ivlefthand.isClickable =false
                        dialogPlacement.ivlefthand.setBackgroundColor(Color.parseColor("#999999"))
                    }
                    "Left Wrist" ->{
                        dialogPlacement.ivleftwrist.alpha =0.3f
                        dialogPlacement.ivleftwrist.isClickable =false
                        dialogPlacement.ivleftwrist.setBackgroundColor(Color.parseColor("#999999"))
                    }
                    "Left Leg" -> {
                        dialogPlacement.ivleftleg.alpha =0.3f
                        dialogPlacement.ivleftleg.isClickable =false
                        dialogPlacement.ivleftleg.setBackgroundColor(Color.parseColor("#999999"))
                    }
                    "Chest" ->{
                        dialogPlacement.ivchest.alpha =0.3f
                        dialogPlacement.ivchest.isClickable =false
                        dialogPlacement.ivchest.setBackgroundColor(Color.parseColor("#999999"))
                    }

                }
            }


    }

    @SuppressLint("SetTextI18n")
    private fun showRetryDialog(type: Int) {
        dialog.dismiss()
        retrydialog = Dialog(this@DataReceiverActivity)
        retrydialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        retrydialog.setCancelable(false)
        showretrydialogBinding= ShowretrydialogBinding.inflate(layoutInflater)
        retrydialog.setContentView(showretrydialogBinding!!.root)
        when(type){
            1 -> {
                showretrydialogBinding?.reasons?.visibility = View.VISIBLE
                showretrydialogBinding?.reason1?.visibility = View.VISIBLE
                showretrydialogBinding?.reason2?.visibility = View.VISIBLE
            }
            2 -> {
                showretrydialogBinding?.reasonhead?.text = "Device Disconnected unfortunately, Please try again. If repeat, Click RESET button and Retry again."
                showretrydialogBinding?.reasons?.visibility = View.GONE
                showretrydialogBinding?.reason1?.visibility = View.GONE
                showretrydialogBinding?.reason2?.visibility = View.GONE
            }
        }
        val display = windowManager.defaultDisplay
        dialog.window?.setLayout((display.width * 0.95).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        showretrydialogBinding?.handler= this@DataReceiverActivity
        retrydialog.show()

    }

    fun createService(){
        val mt_buf = MtBuf(this)
        val callback = CallBack(mt_buf, this)
        chatService = BluetoothChatService(this, callback)
    }

    fun resetbluetooth(){
        if (adapter.isEnabled) {
            adapter.disable()
        }

        GlobalScope.launch {
            delay(2500L)
            adapter.enable()
        }

    }

    override fun onLeftHandClicked(view: View) {

        deviceplacementBinding.ivlefthand.setBackgroundColor(Color.BLUE)
        deviceplacementBinding.ivleftwrist.setBackgroundColor(resources.getColor(R.color.gray))
        deviceplacementBinding.ivleftleg.setBackgroundColor(resources.getColor(R.color.gray))
        deviceplacementBinding.ivchest.setBackgroundColor(resources.getColor(R.color.gray))
        selectedplacement="Left Hand"
    }

    override fun onLeftWristClicked(view: View) {

        deviceplacementBinding.ivleftwrist.setBackgroundColor(Color.BLUE)
        deviceplacementBinding.ivlefthand.setBackgroundColor(resources.getColor(R.color.gray))
        deviceplacementBinding.ivleftleg.setBackgroundColor(resources.getColor(R.color.gray))
        deviceplacementBinding.ivchest.setBackgroundColor(resources.getColor(R.color.gray))
        selectedplacement="Left Wrist"
    }

    override fun onLeftLegClicked(view: View) {
        deviceplacementBinding.ivleftleg.setBackgroundColor(Color.BLUE)
        deviceplacementBinding.ivleftwrist.setBackgroundColor(resources.getColor(R.color.gray))
        deviceplacementBinding.ivlefthand.setBackgroundColor(resources.getColor(R.color.gray))
        deviceplacementBinding.ivchest.setBackgroundColor(resources.getColor(R.color.gray))
        selectedplacement="Left Leg"
    }

    override fun onChestClicked(view: View) {
        deviceplacementBinding.ivchest.setBackgroundColor(Color.BLUE)
        deviceplacementBinding.ivleftwrist.setBackgroundColor(resources.getColor(R.color.gray))
        deviceplacementBinding.ivleftleg.setBackgroundColor(resources.getColor(R.color.gray))
        deviceplacementBinding.ivlefthand.setBackgroundColor(resources.getColor(R.color.gray))
        selectedplacement="Chest"
    }

    override fun onTakeTestClicked(view: View) {
        if(!TextUtils.isEmpty(selectedplacement)){
            dialogPlacement.dismiss()
            val device = adapter.getRemoteDevice(Utility.getSavedBluetoothAddress(this))
            createService()
            chatService?.connect(device)
            showMessageDialog()
        }else{
            Toast.makeText(this, "Please select test type", Toast.LENGTH_LONG).show()
        }
    }

    fun ShowECGGraphActivity(){
        val intent = Intent(this, ECGgraphActivity::class.java)
        intent.putExtra("result1", viewmodel.result1)
        intent.putExtra("result2", viewmodel.result2)
        intent.putExtra("result3", viewmodel.result3)
        intent.putExtra("result4", viewmodel.result4)
        startActivity(intent)
        finish()
    }

    override fun onTestCancelClicked(view: View) {
        finish()
    }


}


