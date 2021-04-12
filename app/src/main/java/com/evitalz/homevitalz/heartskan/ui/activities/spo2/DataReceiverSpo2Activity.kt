package com.evitalz.homevitalz.heartskan.ui.activities.spo2

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.graphics.Color
import android.graphics.DashPathEffect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.heartskan.R
import com.evitalz.homevitalz.heartskan.BR
import com.evitalz.homevitalz.heartskan.Utility
import com.evitalz.homevitalz.heartskan.database.Device_Readings
import com.evitalz.homevitalz.heartskan.databinding.ActivityDataReceiverSpo2Binding
import com.evitalz.homevitalz.heartskan.ui.activities.spo2.BluetoothLeService1.*
import com.evitalz.homevitalz.heartskan.ui.model.DeviceData
import com.evitalz.homevitalz.heartskan.ui.viewmodels.DataReceiveSpo2Viewmodel
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlinx.android.synthetic.main.row_devicenew.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DataReceiverSpo2Activity : AppCompatActivity() {

    lateinit var binding: ActivityDataReceiverSpo2Binding
    val viewModel: DataReceiveSpo2Viewmodel by lazy {
        ViewModelProvider(this).get(DataReceiveSpo2Viewmodel::class.java)
    }
    var ms: Long = 0
    var bluetoothLeService: BluetoothLeService1? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var save_pointer: Int = 0
    private var get_pointer = 0
    private var k = 0
    private val cmdData = ByteArray(100)
    private var sequenceString: String? = null
    private var notifyString: String? = null
    private val mHandler: Handler? = null
    private var readbyes = 0
    private var times = 0
    private var type = "Pulse Oximeter"
    var data_received = false
    var progressDialog: ProgressDialog? = null
    @SuppressLint("SimpleDateFormat")
    var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val mScanning = false
    var referenceval: Long = 0
    var entries = ArrayList<Entry>()
    var lineData: LineData? = null
    var dataSets: LineDataSet? = null
    var lasttime: Long = 0
    var val11:String=""
    var val2:String=""
    var val3:String=""
    var currentDevice: BluetoothDevice? = null
    val spo2list =ArrayList<Int>()
    val hrvlist =ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataReceiverSpo2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ms = System.currentTimeMillis()

        viewModel.deviceDataResult.observe(this, androidx.lifecycle.Observer { data ->
//            binding.setVariable(BR.dataModel, data)
            binding.setVariable(BR.dataModel, data)
            Log.d("deviceresult", "onCreate:$data ")
            viewModel.addResult(data)
            binding.executePendingBindings()
        })
        viewModel.results.observe(this, androidx.lifecycle.Observer {
            setChart(it,0)
        })
        val gattServiceIntent = Intent(this@DataReceiverSpo2Activity, BluetoothLeService1::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())

    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            bluetoothLeService = (service as BluetoothLeService1.LocalBinder).service
            if (!bluetoothLeService?.initialize()!!) {
                finish()
            }
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
         /*   bluetoothLeService!!.connect("C0:26:DA:00:01:47","TNG SPO2", null)*/
            val list = bluetoothManager.adapter.bondedDevices
            list.forEach {
                if (it.name.contains("TNG SPO2")) {
                    bluetoothLeService!!.connect(it.address, it.name, null)
                    currentDevice = it
                }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            unbindService(this)
            bluetoothLeService = null
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_GATT_CONNECTED)
        intentFilter.addAction(ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(ACTION_DATA_AVAILABLE)
        intentFilter.addAction(ACTION_FETCHING_DATA)
        intentFilter.addAction(MISMATCH_READING)
        intentFilter.addAction(ACTION_CONTINUOUS_DATA)
        intentFilter.addAction(ERROR_133)
        intentFilter.addAction(ACTION_DATA_RECEIVED)
        intentFilter.addAction(ACTION_GATT_DEVICE_NOT_FOUND)
        intentFilter.addAction("Data_Received")
        intentFilter.addAction("Receiving_Data")
        return intentFilter
    }

    override fun onPause() {
        super.onPause()
//        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(mGattUpdateReceiver)
    }
    private val mGattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("bluetooth_test", intent.action.toString())
            val action = intent.action
            if (ACTION_GATT_CONNECTED == action) {
            } else if (ACTION_GATT_DEVICE_NOT_FOUND == action) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.DevicenotFound),
                    Toast.LENGTH_SHORT
                ).show()
                currentDevice?.let {
                    bluetoothLeService!!.connect(it.address, it.name, type)
                }
            } else if (ACTION_GATT_DISCONNECTED == action) {
                registerReceiver(this, makeGattUpdateIntentFilter())
                if (!data_received) {
                    val confimation_dialog = Confimation_Dialog_new(
                        this@DataReceiverSpo2Activity,
                        "Device Disconnected unfortunately, Please Retry again. If repeat, Click RESET button and Retry again.",
                        true,
                        true,
                        false
                    )
                    confimation_dialog.show()
                    confimation_dialog.set_yesOnClickListener(View.OnClickListener {
                        currentDevice?.let {
                            bluetoothLeService!!.connect(it.address, it.name, type)
                        }
                        confimation_dialog.dismiss()
                    })
                    confimation_dialog.set_noOnClickListener(View.OnClickListener {
                        try {
                            bluetoothLeService!!.disconnect()
                        } catch (e: Exception) {
                        }
                        confimation_dialog.dismiss()
                    })
                    confimation_dialog.set_maybeOnClickListener(View.OnClickListener {
                        progressDialog?.show()
                        resetbluetooth()
                    })
                    confimation_dialog.set_text_yes_button("Retry")
                    confimation_dialog.set_text_no_button("Cancel")
                    if (mScanning)
                        Toast.makeText(
                            this@DataReceiverSpo2Activity,
                            resources.getString(R.string.DevicenotFound),
                            Toast.LENGTH_LONG
                        ).show()
                }
                currentDevice?.let {
                    bluetoothLeService!!.connect(it.address, it.name, type)
                }
            } else if (ACTION_GATT_SERVICES_DISCOVERED == action) {

            } else if (ACTION_DATA_AVAILABLE == action) {

            } else if (ACTION_DATA_NOT_AVAILABLE == action) {

                Toast.makeText(
                    context,
                    resources.getString(R.string.NoDataFound),
                    Toast.LENGTH_SHORT
                ).show()
                data_received = true
                val info_dialog =
                    Info_Dialog(this@DataReceiverSpo2Activity, "No Data Available", true)
                info_dialog.show()
                info_dialog.setOnClickListener(View.OnClickListener {
                    info_dialog.dismiss()
                    finish()
                })

            } else if (ACTION_CHECKING_DATA == action) {

            } else if (ACTION_SETTING_DEVICE_DATE == action) {

                Toast.makeText(
                    context,
                    resources.getString(R.string.SettingDeviceDate),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (ACTION_CLEARING_DATA == action) {

            } else if (MISMATCH_READING == action) {

                val info_dialog = Info_Dialog(
                    this@DataReceiverSpo2Activity,
                    resources.getString(R.string.Thelastreadingismismatchedwithwhatyouchoosedtype) + type,
                    true
                )
                info_dialog.show()
                info_dialog.setOnClickListener(View.OnClickListener {
                    info_dialog.dismiss()
                    finish()
                })
                Toast.makeText(
                    context,
                    resources.getString(R.string.Thelastreadingismismatchedwithwhatyouchoosedtype) + type,
                    Toast.LENGTH_LONG
                ).show()
            } else if (ERROR_133 == action) {
                //displayData(intent.getStringExtra(BluetoothLeService1.EXTRA_DATA));
                /* tv8.setText("Bluetooth LE Error");
                msg.setText("Bluetooth LE Error");*/
                val info_dialog =
                    Info_Dialog(this@DataReceiverSpo2Activity, "Bluetooth LE Error", false)
                info_dialog.show()
                info_dialog.setOnClickListener(View.OnClickListener {
                    info_dialog.dismiss()
                    finish()
                })
                Toast.makeText(context, "Bluetooth LE Error", Toast.LENGTH_LONG).show()
            } else if (action == ACTION_DATA_RECEIVED){
                data_received = true
                val2 = intent.getIntExtra(PARAM_SP02, 0).toString()
                val11 = intent.getIntExtra(PARAM_PULSE, 0).toString()
                val pi = intent.getFloatExtra(PARAM_PI, 0f)
                val3 =pi.toString()
//                val hf= intent.getIntExtra("hf",0).toString()
//                val lf=intent.getIntExtra("lf",0).toString()
//                Log.d("hflf", "onReceive: "+hf+"ms2"+lf)
                spo2list.add(intent.getIntExtra(PARAM_SP02, 0))
                hrvlist.add(intent.getIntExtra(PARAM_PULSE, 0))
                viewModel.deviceDataResult.value =
                    DeviceData(val11, val2, System.currentTimeMillis() - ms, pi = pi)

                val deviceReadings = Device_Readings(
                        0, 1, Utility.getpregid(application),
                       val2,
                        val11,
                        "", "", Calendar.getInstance().time.time, 0,"SpO2", 7, 1, "",
                        "", "",
                        Calendar.getInstance().time.time
                )
                viewModel.insertreadings(deviceReadings)


            } else if (action == ACTION_CONTINUOUS_DATA) {
                val notify = intent
                    .getByteArrayExtra("data")
                if (notify != null) {
                    getData(notify)
                }
            } else if (action == "Receiving_Data") {
                data_received = true
                intent.getStringExtra("DATE")
                val type = intent.getStringExtra("TYPE")
                intent.getStringExtra("MANUF")
                val11 = intent.getStringExtra("VAL1").toString()
                val2 = intent.getStringExtra("VAL2").toString()
                Log.d("bluetooth_test_data", val11)
                Log.d("bluetooth_test_data", val2)
                val val3 = intent.getStringExtra("VAL3")
                val val4 = intent.getStringExtra("VAL4")
                val val5 = ""

                viewModel.deviceDataResult.value =
                    DeviceData(val11, val2, System.currentTimeMillis() - ms, 0.0f)
//                viewModel.insertreadings(
//                    Device_Readings(
//                        0,
//                        val2,
//                        val11, "",
//                        Calendar.getInstance().time.time
//                    )
//                )

                if (type == "Pulse Oximeter") {

                    val val1 = val11
                    val finalVal = val5
                } else if (action == "Data_Received") {
                    var alertDialog: AlertDialog? = null
                    var nowdd = intent.getStringExtra("DATE")
                    try {
                        val ddd: Date = format.parse(intent.getStringExtra("DATE"))
                        val c = Calendar.getInstance()
                        c.add(Calendar.MONTH, -6)
                        val mmm = c.time
                        if (ddd.before(mmm)) {
                            nowdd = format.format(Calendar.getInstance().time)
                            alertDialog = AlertDialog.Builder(this@DataReceiverSpo2Activity)
                                .setMessage(resources.getString(R.string.DateTimeisnotvalidThedatetimeissettocurrentdatetimeThiscanhappenwhenbatteryisreplacedorfirsttimeofconnectingtodevice))
                                .setCancelable(false)
                                .setPositiveButton(
                                    "OK"
                                ) { dialog, id1 ->
                                    dialog.dismiss()
                                    val i = Intent()
                                    this@DataReceiverSpo2Activity.setResult(RESULT_OK, i)
                                    finish()
                                }
                                .create()
                            alertDialog.show()
                        }

                        try {
                            unregisterReceiver(this)
                        } catch (e: Exception) {
                        }
                        if (!(alertDialog != null && alertDialog.isShowing)) {
                            val i = Intent()
                            this@DataReceiverSpo2Activity.setResult(RESULT_OK, i)
                            finish()
                        }
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        private fun getData(notify: ByteArray) {
            var i = 0
            while (i < notify.size) {
                cmdData[save_pointer++] = notify[i++]
                readbyes++
                if (save_pointer >= cmdData.size)
                    save_pointer = 0
            }
            var bageok = false
            while (save_pointer != get_pointer) {
                if ((cmdData[get_pointer]).equals(0x55)) {
                    val t: Byte = cmdData[(get_pointer + 1) % cmdData.size]
                    if (t in 5..readbyes) {
                        bageok = true
                        break
                    } else {
                        break
                    }
                } else {
                    get_pointer++
                    if (get_pointer >= cmdData.size) get_pointer = 0
                }
                readbyes--
            }
            if (bageok) {
                k = cmdData.get((get_pointer + 1) % cmdData.size).toInt()
                if (k <= 0)
                    Toast.makeText(this@DataReceiverSpo2Activity, "k=0times" + times++, Toast.LENGTH_SHORT).show()
                else {
                    val Data = ByteArray(k)
                    run {
                        var i = 0
                        while (i < k) {
                            Data[i++] = cmdData.get(get_pointer++)
                            readbyes--
                            if (get_pointer >= cmdData.size) get_pointer = 0
                        }
                    }
                    bageok = false
                    val builder = StringBuilder(Data.size)
                    for (i in Data.indices) {
                        builder.append(String.format("%02X ", Data[i]))
                    }
                    notifyString = builder.toString()
                    mHandler?.sendEmptyMessage(8)
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    when (Data[2].toInt()) {
                        0 -> {
                            val sequence = ByteArray(5) //Remove the serial number and display it.
                            sequence[0] = Data[9]
                            sequence[1] = Data[10]
                            sequence[2] = Data[11]
                            sequence[3] = Data[12]
                            sequence[4] = Data[13]
                            val builder1 = StringBuilder(sequence.size)
                            for (bs in sequence) {
                                builder1.append(String.format("%02X ", bs))
                            }
                            sequenceString = builder1.toString()
                            try {
                                Thread.sleep(500)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                        1 -> {
                        }
                        2 ->
                            println("@@@h" + (if ((Data[6] * 256).toShort() + Data[5] >= 0) Data[5] else Data[5] + 256) as Short + "mmHg")
//                    0xEE.toByte() -> println("@@@Received Error")
                        6 -> {
                        }
                        else -> {
                        }
                    }
                }
            }
        }

        private fun resetbluetooth() {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (mBluetoothAdapter.isEnabled) {
                mBluetoothAdapter.disable()
                println("@@@Disable bluetooth")
            }
            Handler().postDelayed({
                mBluetoothAdapter.enable()
                println("@@@Enable bluetooth")
                Handler().postDelayed({
                    //                        progressDialog.dismiss();
                }, 2500)
            }, 2500)
        }
    }

    fun setChart(data: DeviceData, type:Int) {
        val xval: Float = (System.currentTimeMillis() - ms).toFloat()
        when(viewModel.selectedtab){
            0 -> entries.add(Entry(data.time.toFloat(), data.spo2val.toFloat()))
            1 -> entries.add(Entry(data.time.toFloat(), data.pulseval.toFloat()))
        }


        binding.chart.axisRight.isEnabled = false
        binding.chart.description.isEnabled = false
        binding.chart.setTouchEnabled(true)
        binding.chart.isDragEnabled = true
        binding.chart.setScaleEnabled(true)
        binding.chart.setPinchZoom(true)
        binding.chart.setDrawGridBackground(false)
        binding.chart.maxHighlightDistance = 300f
        binding.chart.xAxis.valueFormatter = MyAxisValueFormatter(
                Calendar.getInstance().time.time
        )
        binding.chart.xAxis.setDrawLabels(false)
        binding.chart.xAxis.labelCount = 0
        binding.chart.xAxis.setDrawGridLines(false)
        binding.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.chart.xAxis.axisLineColor = Color.blue(200)
        binding.chart.xAxis.granularity = 4f // only intervals of 1 day
        binding.chart.xAxis.textSize = 8f

//        val y: YAxis = binding.chart.getAxisLeft()
//        y.setDrawLabels(true)
//        y.setLabelCount(3, true)
//        y.textColor = Color.parseColor("#00574B")
//        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
//        y.setDrawGridLines(false)
//        y.axisLineColor = Color.BLACK

        val y: YAxis = binding.chart.axisLeft
//            y.axisMinimum = 0f
        y.setDrawLabels(true)
//            y.setLabelCount(4, true)
        y.textColor = Color.parseColor("#00574B")
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        y.setDrawGridLines(true)
        y.axisLineColor = Color.TRANSPARENT

        dataSets = LineDataSet(entries, "")
        lineData = LineData(dataSets)
        lineData!!.setValueTextColor(Color.BLACK)
        lineData!!.setValueTextSize(8f)
        lineData!!.setDrawValues(false)
        lineData!!.setValueFormatter(MyValueFormatter())
        dataSets!!.cubicIntensity = 1.0f
        dataSets!!.setDrawIcons(false)
        dataSets!!.setDrawCircleHole(false)
        dataSets!!.lineWidth = 2f
        dataSets!!.setDrawCircles(false)
        dataSets!!.circleRadius = 0f
        dataSets!!.setDrawFilled(false)
        dataSets!!.formLineWidth = 1f
        dataSets!!.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        dataSets!!.formSize = 15f

        dataSets!!.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSets!!.color = Color.BLUE
        dataSets!!.setCircleColors(Color.BLUE)



        val l: Legend = binding.chart.getLegend()
        l.formSize = 12f // set the size of the legend forms/shapes
        l.form = Legend.LegendForm.CIRCLE // set what type of form/shape should be used
        l.textSize = 12f
        l.textColor = Color.BLACK
        l.xEntrySpace = 10f // space between the legend entries on the x-axis
        l.yEntrySpace = 5f // space between the legend entries on the y-axis
        val legendEntryA = LegendEntry()
        when(type){
            0 -> legendEntryA.label = "SPO2"
            1 ->  legendEntryA.label = "HRV"

        }
        legendEntryA.form = Legend.LegendForm.SQUARE
        legendEntryA.formColor = Color.rgb(23, 107, 239)
//        val legendEntryB = LegendEntry()
//        legendEntryB.label = "Normal"
//        legendEntryB.formColor = Color.rgb(23, 156, 82)
//        val legendEntryC = LegendEntry()
//        legendEntryC.label = "Fever"
//        legendEntryC.formColor = Color.rgb(255, 62, 48)
//        val legendEntryD = LegendEntry()
//        legendEntryD.label = "Hypothermia"
//        legendEntryD.formColor = Color.rgb(247, 181, 41)
        l.setCustom(Arrays.asList(legendEntryA))
        binding.chart.setData(lineData)
        binding.chart.getLineData().addDataSet(dataSets)
        binding.chart.getData().notifyDataChanged()
        binding.chart.description.isEnabled = false
        binding.chart.legend.isEnabled = true
        binding.chart.notifyDataSetChanged()
        binding.chart.invalidate()
    }

    class MyAxisValueFormatter(private val referenceTimestamp: Long) : IAxisValueFormatter {
        lateinit var mDataFormat: SimpleDateFormat
        lateinit var mDate: Date

        init {
            val mDataFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            val mDate = Date()
        }

        override fun getFormattedValue(value: Float, axis: AxisBase): String {
            var setvalue: String = ""
            val convertedTimestamp = value.toLong()
            val originalTimestamp = referenceTimestamp + convertedTimestamp
//            Log.d("originaltimestamp", originalTimestamp.toString() + "")
            setvalue = getHour(originalTimestamp)
            return setvalue
        }

        private fun getHour(timestamp: Long): String {
            return try {
                mDate.time = timestamp
                mDataFormat.format(mDate)
            } catch (ex: java.lang.Exception) {
                "xx"
            }
        }
    }

    class MyValueFormatter : IValueFormatter {
        override fun getFormattedValue(
            value: Float,
            entry: Entry,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler
        ): String {
            val i = Math.round(value)
            return i.toString() + ""
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

//        var meanhrv:Int=0
//        var meanspo2:Int=0
//        var lowestspo2:Int=0
//        var hf:Float =0F
//        var lf:Float=0F
//        var ratio:Float=0F
//        if(spo2list.size!=0){
//            meanspo2=sum(spo2list)
//            meanhrv = sum(hrvlist)
//            lowestspo2 = Collections.min(spo2list)
//        }
//        if(bluetoothLeService?.list?.size!=0){
//            hf= Collections.min(bluetoothLeService?.list).toFloat()
//            lf= Collections.max(bluetoothLeService?.list).toFloat()
//            ratio= (lf/hf).toFloat()
//            Log.d("ble_test" , "lf "+ Collections.min(bluetoothLeService?.list))
//            Log.d("ble_test" , "hf "+ Collections.max(bluetoothLeService?.list))
//        }
//
//        android.app.AlertDialog.Builder(Analytics@ this)
//            .setIcon(android.R.drawable.ic_dialog_alert)
//            .setMessage("Are you sure you want to Leave Recording?")
//            .setPositiveButton(
//                "Yes"
//            ) { dialog, which ->
//                lasttime = System.currentTimeMillis()
//                gettotalhrs(ms, lasttime)
//                Log.d("totalhrs", "onBackPressed: ${gettotalhrs(ms, lasttime)}")
//                if(!val11.equals("")&&(!val2.equals("")))
////                    viewModel.insertlog(
////                        com.evitalz.homevitalz.evitalzo2.database.Log(
////                            0, lasttime, ms, val2, val11, val3, meanspo2.toString()+"%", lowestspo2.toString()+"%", "",
////                            "", "", "", meanhrv.toString(), hf.toString(), lf.toString(), ratio.toString(), "", "", gettotalhrs(
////                                ms,
////                                lasttime
////                            )
////                        )
////                    )
//                finish()
//            }
//            .setNegativeButton("No", null)
//            .show()
//
//        Log.d("listsize", "onBackPressed: ${spo2list.size}")
//        Log.d("listsize", "onBackPressed: ${hrvlist.size}")
//        Log.d("listsize", "onBackPressed: ${meanspo2}")
//        Log.d("listsize", "onBackPressed: ${meanhrv}")

    }

    fun gettotalhrs(start: Long, end: Long): String{
        val diff=end-start
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return hours.toString()+" "+"hrs"+" "+minutes+" "+"mins"
    }

    fun sum(list: List<Int>): Int {
        var sum = 0
        for (i in list) sum = sum + i
        return sum/(list.size)
    }

}