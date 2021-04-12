package com.evitalz.homevitalz.heartskan.ui.activities.connect_device

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.evitalz.homevitalz.heartskan.R
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.evitalz.homevitalz.heartskan.Utility
import com.evitalz.homevitalz.heartskan.databinding.ActivityECGgraphBinding
import com.evitalz.homevitalz.heartskan.databinding.ConnectDialogBinding

import com.evitalz.homevitalz.heartskan.ui.viewmodels.GraphViewmodel
import kotlinx.coroutines.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class ECGgraphActivity : AppCompatActivity() {

    lateinit var binding: ActivityECGgraphBinding
    lateinit var dialog:Dialog
    lateinit var dialogBinding: ConnectDialogBinding
    val paint = Paint()
    val paint1 = Paint()
    val paint2 = Paint()
    val paint3 = Paint()
    lateinit var canv: Canvas
    lateinit var bitmap: Bitmap
    lateinit var drawingImageView: ZoomableImageView
    lateinit var textview: TextView
    lateinit var file: File
    var x_axis: ArrayList<Float> = ArrayList()
    var y_axis: ArrayList<Float> = ArrayList()
    var result1: Long? = null
    var result2: Long? = null
    var result3: Long? = null
    var result4: Long? = null


    private val viewmodel: GraphViewmodel by lazy {
        ViewModelProvider(
            this
        ).get(GraphViewmodel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityECGgraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewmodel.patientdetails.observe(this, androidx.lifecycle.Observer {
            Log.d("patient data", "onCreate: ${it.pname}")
            Log.d("patient data", "onCreate: ${it.page}")
            Log.d("patient data", "onCreate: ${it.getGender()}")
            Log.d("patient data", "onCreate: ${it.bldgrp}")
            Log.d("patient data", "onCreate: ${it.weight}")
            Log.d("patient data", "onCreate: ${it.height}")
            Log.d("patient data", "onCreate: ${it.diabetic}")
        })

        result1 = intent.extras?.getLong("result1")
        result2 = intent.extras?.getLong("result2")
        result3 = intent.extras?.getLong("result3")
        result4 = intent.extras?.getLong("result4")

        GlobalScope.async {
            result1?.let {
                if (result1 != 0L) {
                    Log.d("graph_test", "resul1")
                    drawCharts(it).await()
                }
            }
            result2?.let {
                if (result2 != 0L) {
                    Log.d("graph_test", "resul2")
                    drawCharts(it).await()
                }
            }

            result3?.let {
                if (result3 != 0L) {
                    Log.d("graph_test", "resul3")
                    drawCharts(it).await()
                }
            }
            result4?.let {
                if (result4 != 0L) {
                    Log.d("graph_test", "resul4")
                    drawCharts(it).await()
                }
            }

        }
        binding.btncreatepdf.setOnClickListener {
            binding.btncreatepdf.isEnabled = false
            GlobalScope.launch {
                val list : ArrayList<Long>  = ArrayList()
                if(result1 != null && result1 != 0L)
                    list.add(result1!!)
                if(result2 != null && result2 != 0L)
                    list.add(result2!!)
                if(result3 != null && result3 != 0L)
                    list.add(result3!!)
                if(result4 != null && result4 != 0L)
                    list.add(result4!!)
                createpdf(list)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.createpdf, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menucreatepdf ->{

                dialog = Dialog(this@ECGgraphActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialogBinding= ConnectDialogBinding.inflate(layoutInflater)
                dialog.setContentView(dialogBinding.root)
                val display = windowManager.defaultDisplay
                dialog.window?.setLayout(
                    (display.width * 0.95).toInt(),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                dialogBinding.cancel.visibility= View.GONE
                dialogBinding.textmsg.text="Creating PDF.. Please Wait"
                dialog.show()
                GlobalScope.launch {
                    val list : ArrayList<Long>  = ArrayList()
                    if(result1 != null && result1 != 0L)
                        list.add(result1!!)
                    if(result2 != null && result2 != 0L)
                        list.add(result2!!)
                    if(result3 != null && result3 != 0L)
                        list.add(result3!!)
                    if(result4 != null && result4 != 0L)
                        list.add(result4!!)
                    createpdf(list)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
    suspend fun drawCharts(result: Long) = lifecycleScope.async {
        withContext(Dispatchers.Main) {
            drawingImageView = ZoomableImageView(this@ECGgraphActivity)

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200
            )
            drawingImageView.layoutParams = layoutParams
            drawingImageView.scaleType = ImageView.ScaleType.FIT_XY
            textview = TextView(this@ECGgraphActivity)
            binding.linEcgActivity.addView(textview)
            binding.linEcgActivity.addView(drawingImageView)
            bitmap = Bitmap.createBitmap(2345, 420, Bitmap.Config.ARGB_8888)
            canv = Canvas(bitmap)
            drawingImageView.setImageBitmap(bitmap)

            paint.color = Color.rgb(0, 0, 0)
            paint.strokeWidth = 2f

            paint1.color = Color.rgb(207, 111, 110)
            paint1.strokeWidth = 1f

            paint2.color = Color.rgb(207, 111, 110)
            paint2.strokeWidth = 3f

            paint3.color = Color.rgb(207, 111, 110)
            paint3.strokeWidth = 2f

            draw(drawingImageView, bitmap, paint, paint1, paint3, canv, result)
        }
    }


    suspend fun draw(
        drawingImageView: ZoomableImageView,
        bitmap: Bitmap,
        paint: Paint,
        paint1: Paint,
        paint3: Paint,
        canv: Canvas,
        rowId: Long
    ) {
        withContext(Dispatchers.IO) {
            val deviceReadings = viewmodel.getdata(rowId)
            withContext(Dispatchers.Main) {
                Log.d("dread5", "draw: ${deviceReadings.dread5}")
                val data = deviceReadings.dread5
                val list: List<String> = data.split(",")
                val split = 2500 / 500
                var startx: Float
                var starty: Float
                var endx: Float
                var endy: Float
                val center = 280
                starty = center.toFloat()
                startx = 45f

                var i = 45
                while (i <= 2295) {
                    publishProgress(
                        i.toFloat().toString(),
                        "45f",
                        i.toFloat().toString(),
                        "405f",
                        "graph",
                        i.toString(),
                        paint,
                        paint1,
                        paint3,
                        canv
                    )
                    i += split
                }


                i = 45
                while (i <= 405) {
                    publishProgress(
                        "45f",
                        i.toFloat().toString(),
                        "2295f",
                        i.toFloat().toString(),
                        "graph",
                        i.toString(),
                        paint,
                        paint1,
                        paint3,
                        canv
                    )
                    i += split
                }


                i = 0
                while (i + 1 < list.size) {
                    var tx: Int = list.get(i).toInt()
                    var ty: Int
                    //                    String ty=array.get(i+1);
                    if (tx < 0) {
                        tx += 256
                    }
                    ty = list.get(i + 1).toInt() * 256 + tx
                    endx = ((startx + 0.9).toFloat())
                    endy = Math.abs(center - (ty - 16000) / 7).toFloat()
                    //                    endy = (((((ty -16000.0f)))));
                    if (i == 0) starty = endy
                    publishProgress(
                        startx.toString(),
                        starty.toString(),
                        endx.toString(),
                        endy.toString(),
                        "line",
                        "",
                        paint,
                        paint1,
                        paint3,
                        canv
                    )
                    startx = endx
                    starty = endy
                    x_axis.add(endx)
                    y_axis.add(endy)
                    i += 2
                }
                textview.text="Device Placement :" +deviceReadings.dread3+ "       "+ "HeartRate :" + deviceReadings.dread2+" bpm" + "    "+ deviceReadings.dread1
                setChart(drawingImageView, bitmap)
            }
        }
    }

    private fun setChart(drawingImageView: ZoomableImageView, bitmap: Bitmap) = lifecycleScope.launch(
        Dispatchers.Main
    ) {
        drawingImageView.setImageBitmap(bitmap)
    }

    private fun publishProgress(
        s1: String,
        s2: String,
        s3: String,
        s4: String,
        s5: String,
        s6: String,
        paint: Paint,
        paint1: Paint,
        paint3: Paint,
        canv: Canvas
    ) {
        if (s5 == "graph") {
            if (s6.toInt() % 45 == 0) {
                canv.drawLine(s1.toFloat(), s2.toFloat(), s3.toFloat(), s4.toFloat(), paint3)
            } else {
                canv.drawLine(s1.toFloat(), s2.toFloat(), s3.toFloat(), s4.toFloat(), paint1)
            }
        } else {
            canv.drawLine(s1.toFloat(), s2.toFloat(), s3.toFloat(), s4.toFloat(), paint)
        }
    }

    suspend fun createpdf(list: List<Long>){
        val date1 = Utility.alarmdateformat.format(Calendar.getInstance().time)
        var time:kotlin.String? = Utility.alarmtimeformat.format(Calendar.getInstance().time)
        var patientname : String? = "Bincy Paul"
        var age:kotlin.String? = "23"
        var gender:kotlin.String? = "Female"
        var height:kotlin.String? = "154 cm"
        var weight:kotlin.String? = "60 kg"
        var bbmi:kotlin.String? = "24.5(Normal)"
        var bloodgroup:kotlin.String? = "O +ve"
        var waist:kotlin.String? = "32 in"
        var hip:kotlin.String? = "34 in"
        var diabetic:kotlin.String? = "Type1"
        var hba1c:kotlin.String? = "6.3 %"
        var dia:kotlin.String? = "120 mmHg"
        var sys:kotlin.String? = "80 mmHg"
        var spo2:kotlin.String? = "spo2"
        var oximeterpr:kotlin.String? = "oxim"
        var ecg:kotlin.String? = "ecg"
        var temp:kotlin.String? = "temp"
        var bmi:kotlin.String? = "BMI"
        val test_id = ""
        var test_date:kotlin.String? = ""
        val file_name = ""
        var file: File? = null
        var paint: Paint? = null
        var paint1:android.graphics.Paint? = null
        var paint2:android.graphics.Paint? = null
        var paint3:android.graphics.Paint? = null
        val page = ""


        ecg = "\n\t\t\ndata\n\n(Normal Ranges: 60 - 100)"

        paint = Paint()
        paint.color = Color.rgb(0, 0, 0)
        paint.strokeWidth = 3f
        paint1 = Paint()
        paint1.color = Color.rgb(207, 111, 110)
        paint1.strokeWidth = 1f
        paint2 = Paint()
        paint2.color = Color.rgb(207, 111, 110)
        paint2.strokeWidth = 3f
        paint3 = Paint()
        paint3.color = Color.rgb(207, 111, 110)
        paint3.strokeWidth = 2f
        paint.textSize = 40f
        paint.strokeWidth = 3f

        val white = BaseColor(255, 255, 255)
        val black = BaseColor(0, 0, 0)
        val regularblack = Font(Font.FontFamily.HELVETICA, 10f)
        regularblack.color = black
        val smallblack: Font = Font(Font.FontFamily.HELVETICA, 9f)
        smallblack.color = black
        val small: Font = Font(Font.FontFamily.HELVETICA, 9f)
        small.color = white
        val doc = Document()
        val outputDir = "$filesDir/test/"
        val f = File(outputDir)
        if (!f.isDirectory) {
            f.mkdir()
        }

        try {
            file = File.createTempFile("test", ".pdf", File(outputDir))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val bitDw= getBitmapFromVectorDrawable(
            this@ECGgraphActivity,
            R.drawable.logout
        )!!

        try {
            val writer = PdfWriter.getInstance(doc, FileOutputStream(file))
            doc.pageSize = PageSize.A4.rotate()
            doc.setMargins(30f, 30f, 30f, 30f)
            doc.open()

            val bold: Font = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
            bold.color = white

            val mTitleChunk = Chunk(resources.getString(R.string.app_name))
            val mTitleParagraph = Paragraph(mTitleChunk)
            mTitleParagraph.alignment = Element.ALIGN_CENTER
            doc.add(mTitleParagraph)

            var table = PdfPTable(3)
            table.setTotalWidth(floatArrayOf(100f, 150f, 50f))
            table.widthPercentage = 100f


            var stream1 = ByteArrayOutputStream()
            bitDw.compress(Bitmap.CompressFormat.PNG, 100, stream1)
            var imglogo = Image.getInstance(stream1.toByteArray())
            imglogo.scalePercent(4f)

            var p = Phrase()
            var cell2 = PdfPCell(p)
            cell2.addElement(imglogo)
            cell2.borderWidthBottom = 0f
            cell2.borderWidthRight = 0f
            cell2.borderWidthLeft = 0f
            cell2.borderWidthTop = 0f
            table.addCell(cell2)

            p = Phrase()
            var cell1 = PdfPCell(p)
            cell1.borderWidthBottom = 0f
            cell1.borderWidthLeft = 0f
            cell1.borderWidthRight = 0f
            cell1.borderWidthTop = 0f
            table.addCell(cell1)

            p = Phrase()
            p.add(Chunk("Date:$date1\n\n"))
            p.add(Chunk("Time:$time"))

            var cell = PdfPCell(p)
            cell.borderWidthBottom = 0f
            cell.borderWidthLeft = 0f
            cell.borderWidthTop = 0f
            cell.borderWidthRight = 0f
            table.addCell(cell)

            cell1.borderWidthBottom = 0f
            cell = PdfPCell(p)
            cell.rowspan = 2
            cell.setPadding(5f)
            cell.borderWidthTop = 0f

            p = Phrase("")
            cell = PdfPCell(p)
            cell.borderWidthTop = 0f
            cell.borderWidthBottom = 0f
            cell.borderWidthRight = 0f
            table.addCell(cell)

            p = Phrase()
            cell = PdfPCell(p)
            cell.borderWidthTop = 0f
            cell.borderWidthBottom = 0f
            cell.borderWidthRight = 0f
            cell.borderWidthLeft = 0f
            table.addCell(cell)

            p = Phrase()
            p.add(Chunk(""))
            p.add(Chunk(""))
            cell = PdfPCell(p)
            cell.borderWidthTop = 0f
            cell.borderWidthBottom = 0f
            cell.borderWidthRight = 0f
            cell.borderWidthLeft = 0f
            cell.borderWidthLeft = 0f
            table.addCell(cell)
            doc.add(table)

            val table2 = PdfPTable(3)
            table2.setTotalWidth(floatArrayOf(1f, 1f, 1f))
            table2.widthPercentage = 100f

            var tablecell1 = PdfPTable(2)
            tablecell1.setTotalWidth(floatArrayOf(1.7f, 3f))
            val celldetails = PdfPCell()
            val pics: ByteArray? = null
            if (pics != null && pics.size > 0) {
                val stream = ByteArrayOutputStream()
                val bm = BitmapFactory.decodeByteArray(pics, 0, pics.size)
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imgSoc = Image.getInstance(stream.toByteArray())
                imgSoc.scalePercent(70f)
                celldetails.backgroundColor = BaseColor(3, 156, 186)
                celldetails.addElement(imgSoc)
            } else {
                val stream = ByteArrayOutputStream()
                val bm = BitmapFactory.decodeResource(resources, R.drawable.noimg)
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imgSoc = Image.getInstance(stream.toByteArray())
                imgSoc.scalePercent(10f)
                celldetails.backgroundColor = BaseColor(3, 156, 186)
                celldetails.addElement(imgSoc)
            }
            celldetails.border = 0
            celldetails.setPadding(5f)
            tablecell1.addCell(celldetails)
            p = Phrase()
            p.add(Chunk("Patient Info\n\n", bold))
            p.add(Chunk("Name          : ${viewmodel.patientdetails.value?.pname}\n\n", small))
            p.add(Chunk("Age             : ${viewmodel.patientdetails.value?.page}\n\n", small))
            p.add(Chunk("Gender        : ${viewmodel.patientdetails.value?.getGender()}\n\n", small))
            p.add(Chunk("Bloodgroup  : ${viewmodel.patientdetails.value?.bldgrp}\n\n", small))
            var cell4 = PdfPCell(p)
            cell4.backgroundColor = BaseColor(3, 156, 186)
            cell4.border = 0
            cell4.setPadding(5f)
            tablecell1.addCell(cell4)
            val first = PdfPCell(tablecell1)
            first.border = 0
            first.setPadding(10f)
            table2.addCell(first)
            tablecell1 = PdfPTable(2)
            tablecell1.setTotalWidth(floatArrayOf(1.5f, 3f))


            var cellecg = PdfPCell(tablecell1)
            p = Phrase("ECG\n", bold)
            var p1 = Paragraph("ECG\n ", bold)
            p1.alignment = Element.ALIGN_CENTER
            cellecg.addElement(p1)
            var bmpecg: Bitmap = getBitmapFromVectorDrawable(
                this@ECGgraphActivity,
                R.drawable.heartattack
            )!!
            stream1 = ByteArrayOutputStream()
            bmpecg.compress(Bitmap.CompressFormat.PNG, 100, stream1)
            imglogo = Image.getInstance(stream1.toByteArray())
            imglogo.scalePercent(10f)
            imglogo.alignment = Element.ALIGN_CENTER
            cellecg.setPadding(5f)
            cellecg.backgroundColor = BaseColor(3, 156, 186)
            cellecg.addElement(imglogo)
            cellecg.border = 0
            tablecell1.addCell(cellecg)
            p = Phrase()
            p.add(Chunk(ecg, small))
            cell4 = PdfPCell(p)
            cell4.backgroundColor = BaseColor(3, 156, 186)
            cell4.border = 0
            tablecell1.addCell(cell4)


            val second = PdfPCell(tablecell1)
            second.border = 0
            second.setPadding(10f)
            second.paddingBottom = 0f
            table2.addCell(second)
            tablecell1 = PdfPTable(2)
            tablecell1.setTotalWidth(floatArrayOf(1.5f, 3f))

            val cellpressure = PdfPCell(tablecell1)
            p = Phrase("BMI\n", bold)
            p1 = Paragraph("BMI\n ", bold)
            p1.alignment = Element.ALIGN_CENTER
            cellpressure.addElement(p1)
            val bmpbloodpressure: Bitmap =
                getBitmapFromVectorDrawable(this@ECGgraphActivity, R.drawable.weight)!!
            val bloodstream1 = ByteArrayOutputStream()
            bmpbloodpressure.compress(Bitmap.CompressFormat.PNG, 100, bloodstream1)
            val bloodlogo = Image.getInstance(bloodstream1.toByteArray())
            bloodlogo.scalePercent(9f)
            bloodlogo.alignment = Element.ALIGN_CENTER
            cellpressure.backgroundColor = BaseColor(3, 156, 186)
            cellpressure.addElement(bloodlogo)
            cellpressure.border = 0
            tablecell1.addCell(cellpressure)
            p = Phrase()
            p.add(Chunk("\n\nHeight     : ${viewmodel.patientdetails.value?.height}\n\n", small))
            p.add(Chunk("Weight    : ${viewmodel.patientdetails.value?.weight}\n\n", small))
//            p.add(Chunk("BMI          : $bbmi\n\n", small))
            cell4 = PdfPCell(p)
            cell4.backgroundColor = BaseColor(3, 156, 186)
            cell4.border = 0
            tablecell1.addCell(cell4)

            val third = PdfPCell(tablecell1)
            third.border = 0
            third.setPadding(10f)
            table2.addCell(third)
            cell = PdfPCell(p)
            cell.rowspan = 2
            cell.setPadding(5f)
            cell.borderWidthTop = 0f
            tablecell1 = PdfPTable(2)
            tablecell1.setTotalWidth(floatArrayOf(1.5f, 3f))


            val celloximeter = PdfPCell(tablecell1)
            p = Phrase("WHR\n", bold)
            p1 = Paragraph("WHR\n", bold)
            p1.alignment = Element.ALIGN_CENTER
            celloximeter.addElement(p1)
            val bmpoximeter: Bitmap =
                getBitmapFromVectorDrawable(this@ECGgraphActivity, R.drawable.waist)!!
            stream1 = ByteArrayOutputStream()
            bmpoximeter.compress(Bitmap.CompressFormat.PNG, 100, stream1)
            imglogo = Image.getInstance(stream1.toByteArray())
            imglogo.scalePercent(9f)
            imglogo.alignment = Element.ALIGN_CENTER
            celloximeter.setPadding(5f)
            celloximeter.backgroundColor = BaseColor(3, 156, 186)
            celloximeter.addElement(imglogo)
            celloximeter.border = 0
            tablecell1.addCell(celloximeter)
            p = Phrase()
            p.add(Chunk("\n\nWaist :${viewmodel.patientdetails.value?.waist}\n\n", small))
            p.add(Chunk("Hip    :${viewmodel.patientdetails.value?.hip}\n\n\n", small))
            cell4 = PdfPCell(p)
            cell4.backgroundColor = BaseColor(3, 156, 186)
            cell4.border = 0
            tablecell1.addCell(cell4)


            val fourth = PdfPCell(tablecell1)
            fourth.border = 0
            fourth.setPadding(10f)
            fourth.paddingBottom = 0f
            table2.addCell(fourth)
            tablecell1 = PdfPTable(2)
            tablecell1.setTotalWidth(floatArrayOf(1.5f, 3f))
            cellecg = PdfPCell(tablecell1)
            p = Phrase("Diabetic\n", bold)
            p1 = Paragraph("Diabetic ", bold)
            p1.alignment = Element.ALIGN_CENTER
            cellecg.addElement(p1)
            bmpecg = getBitmapFromVectorDrawable(this@ECGgraphActivity, R.drawable.blood)!!
            stream1 = ByteArrayOutputStream()
            bmpecg.compress(Bitmap.CompressFormat.PNG, 100, stream1)
            imglogo = Image.getInstance(stream1.toByteArray())
            imglogo.scalePercent(10f)
            imglogo.alignment = Element.ALIGN_CENTER
            cellecg.backgroundColor = BaseColor(3, 156, 186)
            cellecg.addElement(imglogo)
            cellecg.border = 0
            tablecell1.addCell(cellecg)
            p = Phrase()
            p.add(Chunk("\n\nDiabetic type: ${viewmodel.patientdetails.value?.diabetic}\n\n", small))
            p.add(Chunk("HbA1c level : ${viewmodel.patientdetails.value?.hba1c}\n\n", small))
            cell4 = PdfPCell(p)
            cell4.backgroundColor = BaseColor(3, 156, 186)
            cell4.border = 0
            tablecell1.addCell(cell4)


            val fifth = PdfPCell(tablecell1)
            fifth.border = 0
            fifth.setPadding(10f)
            fifth.paddingBottom = 0f
            table2.addCell(fifth)
            tablecell1 = PdfPTable(2)
            tablecell1.setTotalWidth(floatArrayOf(1.5f, 3f))

            val celltemp = PdfPCell(tablecell1)
            p = Phrase("BP\n", bold)
            p1 = Paragraph("BP", bold)
            p1.alignment = Element.ALIGN_CENTER
            celltemp.addElement(p1)
            val bmptemp: Bitmap = getBitmapFromVectorDrawable(
                this@ECGgraphActivity,
                R.drawable.bp
            )!!
            stream1 = ByteArrayOutputStream()
            bmptemp.compress(Bitmap.CompressFormat.PNG, 100, stream1)
            imglogo = Image.getInstance(stream1.toByteArray())
            imglogo.scalePercent(9f)
            imglogo.alignment = Element.ALIGN_CENTER
            celltemp.backgroundColor = BaseColor(3, 156, 186)
            celltemp.addElement(imglogo)
            celltemp.border = 0
            tablecell1.addCell(celltemp)
            p = Phrase()
            p.add(Chunk("\n\nSystolic  : ${viewmodel.patientdetails.value?.getsys()}\n\n", small))
            p.add(Chunk("Diastolic : ${viewmodel.patientdetails.value?.getdia()}\n\n", small))
            cell4 = PdfPCell(p)
            cell4.backgroundColor = BaseColor(3, 156, 186)
            cell4.border = 0
            tablecell1.addCell(cell4)

            val sixth = PdfPCell(tablecell1)
            sixth.border = 0
            sixth.setPadding(10f)
            sixth.paddingBottom = 0f
            table2.addCell(sixth)
            doc.add(table2)
            val font2 = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD)

            withContext(Dispatchers.IO){
                for(i in list) {
                    val deviceReadings = viewmodel.getdata(i)
                        val p1 =Paragraph("Device Placement: " + deviceReadings.dread3 + "    "+ "HeartRate :"+ "    "+ deviceReadings.dread2+" bpm" + "  "+deviceReadings.dread1)
                        p1.alignment= Element.ALIGN_CENTER
                        doc.add(p1)
                        val bitmap = Bitmap.createBitmap(2345, 435, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(bitmap)
                        val array = deviceReadings.dread5.split(",".toRegex())
                        var startx: Float
                        var starty: Float
                        var endx: Float
                        var endy: Float
                        try {
                            println(
                                "@@@" + windowManager
                                    .defaultDisplay.height
                            )
                            startx = 45f
                            val width = windowManager
                                .defaultDisplay.width
                            val height = windowManager
                                .defaultDisplay.height
                            val center = 300
                            starty = center.toFloat()
                            val split = 2500 / 500
                            var rr_count = 0
                            var current = 0f
                            val previous = 0f
                            var neg = false
                            val avg = 0.0
                            val peak_point = java.util.ArrayList<Int>()
                            val diff_point = java.util.ArrayList<Int>()
                            run {
                                var i = 45
                                while (i <= 2295) {
                                    onProgressUpdate(
                                        java.lang.Float.toString(i.toFloat()),
                                        java.lang.Float.toString(45f),
                                        java.lang.Float.toString(i.toFloat()),
                                        java.lang.Float.toString(405f),
                                        "graph",
                                        Integer.toString(i), canvas = canvas
                                    )
                                    i = i + split
                                }
                            }
                            run {
                                var i = 45
                                while (i <= 405) {
                                    onProgressUpdate(
                                        java.lang.Float.toString(45f),
                                        java.lang.Float.toString(i.toFloat()),
                                        java.lang.Float.toString(2295f),
                                        java.lang.Float.toString(i.toFloat()),
                                        "graph",
                                        Integer.toString(i), canvas = canvas
                                    )
                                    i = i + split
                                }
                            }
                            var i = 0
                            while (i + 1 < array.size) {
                                var tx: Int = array.get(i).toInt()
                                var ty = 0
                                //                    String ty=array.get(i+1);
                                if (tx < 0) {
                                    tx = 256 + tx
                                }
                                ty = array.get(i + 1).toInt() * 256 + tx
                                endx = (startx + 0.9).toFloat()
                                endy = Math.abs(center - (ty - 16000) / 7).toFloat()
                                if (i == 0) {
                                    current = endy
                                } else {
                                    if (!neg) {
                                        if (endy > current && endy < 170) {
                                            peak_point.add(i / 2)
                                            current = endy
                                            neg = true
                                            rr_count++
                                        }
                                        if (current > endy) current = endy
                                    } else {
                                        if (current > endy && i / 2 - peak_point[peak_point.size - 1] > 20) {
                                            neg = false
                                        } else current = endy
                                    }
                                }
                                if (i == 0) starty = endy
                                println("@@@$tx $ty")
                                println("@@@yval$endy")
                                onProgressUpdate(
                                    java.lang.Float.toString(startx),
                                    java.lang.Float.toString(starty),
                                    java.lang.Float.toString(endx),
                                    java.lang.Float.toString(endy),
                                    "line",
                                    canvas = canvas
                                )
                                //                                Thread.sleep(5);
                                startx = endx
                                starty = endy
                                i = i + 2
                            }
                            onProgressUpdate(
                                "end",
                                " ",
                                "",
                                "",
                                "",
                                "",
                                canvas = canvas
                            )

                            table = PdfPTable(1)
                            table.widthPercentage = 100f
                            var para = Paragraph()
                            p = Phrase("ECG", font2)
                            para.add(p)
                            para.alignment = Element.ALIGN_CENTER
                            val pdfPCell = PdfPCell()
                            pdfPCell.addElement(para)
                            pdfPCell.borderWidthRight = 0f
                            pdfPCell.borderWidthLeft = 0f
                            pdfPCell.borderWidthTop = 0f
                            pdfPCell.borderWidthBottom = 0f
                            //                            table.addCell(pdfPCell);
                            stream1 = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream1)
                            val image = Image.getInstance(stream1.toByteArray())
                            image.scalePercent(30f)
                            image.alignment = Element.ALIGN_CENTER
                            var celll = PdfPCell()
                            celll.borderWidthRight = 0f
                            celll.borderWidthLeft = 0f
                            celll.borderWidthTop = 0f
                            celll.borderWidthBottom = 0f
                            celll.addElement(image)
                            table.addCell(celll)
                            para = Paragraph()
                            p = Phrase("Speed: 25.0mm/s Gain:10mm/mV Heart rate: data", font2)
                            para.add(p)
                            para.alignment = Element.ALIGN_CENTER
                            celll = PdfPCell(para)
                            celll.borderWidthRight = 0f
                            celll.borderWidthLeft = 0f
                            celll.borderWidthTop = 0f
                            celll.borderWidthBottom = 0f
                            val nesting = PdfPTable(1)
                            cell2 = PdfPCell(table)
                            cell2.border = PdfPCell.NO_BORDER
                            nesting.addCell(cell2)
                            doc.add(nesting)
                        } catch (e: Exception) {
                            println("@@@$e")
                        }

                }
            }

            val te = PdfPTable(1)
            te.widthPercentage = 100f
            var para = Paragraph()
            p = Phrase()
            p.add(
                Chunk(
                    "All Information provided on evitalz.com is only intended as a guideline and not as a specific medical protocol.Every actual medical condition or situation,either emergency or non-emergency,is  ",
                    smallblack
                )
            )
            p.add(
                Chunk(
                    "unique o each individual('patient/crew'),and requires the clinical judgement of a qualified physician.For more information or clarification ,we would recommend that the patient/crew should contact",
                    smallblack
                )
            )
            p.add(Chunk("their personal physician", smallblack))
            var c = PdfPCell(p)
            c.border = 0
            te.addCell(c)
//            para = Paragraph("Viewed By: ", font2)
//            p = Phrase("Viewed By: ", font2)
//            para.alignment = Element.ALIGN_RIGHT
//            //                para.add(p);
//            c = PdfPCell(para)
//            c.horizontalAlignment = Element.ALIGN_RIGHT
//            c.border = 0
//            te.addCell(c)
            doc.add(te)
            doc.close()

            withContext(Dispatchers.Main){
                binding.btncreatepdf.isEnabled = true
            }
            dialog.dismiss()

            val intent = Intent(this@ECGgraphActivity, ViewPdfActivity::class.java)
            intent.putExtra("file_name", file.toString())
            startActivity(intent)
        } catch (e: DocumentException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onProgressUpdate(vararg values: String, canvas: Canvas) {
        if (values[4] == "graph") {
            if (values[5].toInt() % 45 == 0) {
                canvas.drawLine(
                    values[0].toFloat(),
                    values[1].toFloat(),
                    values[2].toFloat(),
                    values[3].toFloat(),
                    paint3
                )
            } else {
                canvas.drawLine(
                    values[0].toFloat(),
                    values[1].toFloat(),
                    values[2].toFloat(),
                    values[3].toFloat(),
                    paint1
                )
            }
        } else if (values[0] == "end") {
            paint.textSize = 25f
            paint.strokeWidth = 5f
            canvas.drawText(values[1], 100f, 85f, paint)
        } else if (values[0] == "end1") {
            paint.textSize = 25f
            paint.strokeWidth = 5f
            canvas.drawText(values[1], 100f, 400f, paint)
        } else {
            canvas.drawLine(
                values[0].toFloat(),
                values[1].toFloat(),
                values[2].toFloat(),
                values[3].toFloat(),
                paint
            )
        }
//        canvas = Canvas(bitmap)
    }

    private fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap? {
        var drawable = ContextCompat.getDrawable(context!!, drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}