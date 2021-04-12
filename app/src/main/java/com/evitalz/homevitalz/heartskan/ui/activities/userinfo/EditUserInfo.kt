package com.evitalz.homevitalz.heartskan.ui.activities.userinfo

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.library.BuildConfig
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.heartskan.BR
import com.evitalz.homevitalz.heartskan.R
import com.evitalz.homevitalz.heartskan.UserInfoHandler
import com.evitalz.homevitalz.heartskan.Utility
import com.evitalz.homevitalz.heartskan.databinding.ActivityEditUserInfoBinding
import java.io.*
import java.util.*


class EditUserInfo : AppCompatActivity() , UserInfoHandler {

    lateinit var binding: ActivityEditUserInfoBinding
    var unit =  ArrayList<String>()
    var unit1 =  ArrayList<String>()
    lateinit var dataAdapter : ArrayAdapter<String>
    var selectedItem : String=""
    private val viewmodel: EditUserInfoViewModel by lazy{
        ViewModelProvider(this).get(EditUserInfoViewModel::class.java)
    }
    private val mPhotoUri by lazy {
        contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        )
    }
     val PERMISSION_ID: Int = 100
    var age :Int =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.handler= this
        binding.setVariable(BR.viewmodel, viewmodel)

        unit.add("Type 1")
        unit.add("Type 2")
        unit.add("Prediabetes")
        unit.add("Gestational")
        unit.add("None")
        unit.sort()

        unit1.add("A Positive")
        unit1.add("B Positive")
        unit1.add("O Positive")
        unit1.add("AB Positive")
        unit1.add("A Negative")
        unit1.add("B Negative")
        unit1.add("AB Negative")
        unit1.add("O Negative")
        unit1.sort()

        viewmodel.patientdetails.observe(this, androidx.lifecycle.Observer {
            binding.patient = it
        })
        viewmodel.patientdetails1.observe(this, androidx.lifecycle.Observer {
            binding.patientdetails = it
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.appbarmenu1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnsave -> {
                onSaveClicked()
            }


        }
        return true
    }
    override fun onEditClicked(view: View) {
        TODO("Not yet implemented")
    }

    override fun onbloodgroupclicked(view: View) {

        dataAdapter = ArrayAdapter(this, R.layout.simple_drop_down_item, unit1)
        binding.etbloodgroup.setAdapter(dataAdapter)
        binding.etbloodgroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val bloodgrp = parent.getItemAtPosition(position).toString()
                viewmodel.patientdetails.value!!.diabetic= bloodgrp
                binding.etdiabetictype.setText(bloodgrp)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

    }

    override fun onDobClicked(view: View) {
        val today = Calendar.getInstance()
        DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                Log.d("datetimeSet", "year $year month $month , dayofmonth $dayOfMonth")
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                age =getAge(year,month,dayOfMonth)
                viewmodel.patientdetails.value!!.page= age
                viewmodel.patientdetails.value!!.pdob= Utility.alarmdateformat.format(cal.time)
                binding.etdob.setText(Utility.alarmdateformat.format(cal.time))
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onGenderClicked(view: View) {
//        val popupMenu = PopupMenu(this, binding.etgender)
//        popupMenu.menuInflater.inflate(R.menu.popupmenugender, popupMenu.menu)
//        popupMenu.setOnMenuItemClickListener { item ->
//            val gender =item.title.toString()
//            var gen=0
//            when(gender){
//                "Male"-> gen=0
//                "Female"-> gen=1
//                "Others"-> gen=2
//            }
//           viewmodel.patientdetails.value!!.pgender= gen
//            viewmodel.strgender.set(gender)
//            true
//        }
//        popupMenu.show()
    }

    override fun onDiabetictypeClicked(view: View) {

        dataAdapter = ArrayAdapter(this, R.layout.simple_drop_down_item, unit)
        binding.etdiabetictype.setAdapter(dataAdapter)
        viewmodel.patientdetails1.value!!.bmi = binding.etdiabetictype.text.toString()
    }

    override fun onImagePickClicked(view: View) {
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) if (resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            val filePath: String = getPath(selectedImage)
//            val imageString = compressImage(File(filePath))
            val file_extn = filePath.substring(filePath.lastIndexOf(".") + 1)
            try {
                if (file_extn == "img" || file_extn == "jpg" || file_extn == "jpeg" || file_extn == "png") {
                    Log.d("imagepicker", "onActivityResult: $file_extn")
//                    if(imageString!= null)
//                    viewmodel.patientdetails.value!!.pimage=imageString
//                    val bytes = imageString!!.toByteArray()
                    binding.ivuserimage.setImageURI(selectedImage)
                } else {

                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    fun compressImage(file : File) : String?{
        val MAX_IMAGE_SIZE =1 * 1024
        if (file.length() > MAX_IMAGE_SIZE ) {
            var streamLength = MAX_IMAGE_SIZE
            var compressQuality = 10
            val bmpStream = ByteArrayOutputStream()
            while (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
                bmpStream.use {
                    it.flush()
                    it.reset()
                }

                compressQuality -= 5
                val bitmap = BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options())
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                if (BuildConfig.DEBUG) {
                    Log.d("test upload", "Quality: $compressQuality")
                    Log.d("test upload", "Size: $streamLength")
                }
            }

            FileOutputStream(file).use {
                it.write(bmpStream.toByteArray())
            }
            return Base64.encodeToString(bmpStream.toByteArray() ,Base64.DEFAULT)
        }
        return null
    }

    fun getPath(uri: Uri?): String {
        val projection = arrayOf(MediaColumns.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor
            .getColumnIndexOrThrow(MediaColumns.DATA)
        cursor.moveToFirst()
        val imagePath = cursor.getString(column_index)
        return cursor.getString(column_index)
    }

    fun getImageString(fileName : String)  : String{
        val inputStream: InputStream = FileInputStream(fileName) // You can get an inputStream using any I/O API

        val bytes: ByteArray
        val buffer = ByteArray(8192)
        var bytesRead = 0
        val output = ByteArrayOutputStream()

        try {
            while (inputStream.read(buffer).also({ bytesRead = it }) != -1) {
                output.write(buffer, 0, bytesRead)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        bytes = output.toByteArray()
        val encodedString: String = Base64.encodeToString(bytes ,Base64.DEFAULT)
        Log.d("Imagetostring", "getImageString: $encodedString")
        return encodedString
    }

    fun onSaveClicked(){

        viewmodel.patientdetails.value!!.bp = binding.etsys.text.toString()+ "/"+binding.etdia.text.toString()
        viewmodel.updatepatientprofile()
        finish()

    }

    private fun getAge(year: Int, month: Int, day: Int): Int {

        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        dob.set(year, month, day)
        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        val ageInt = age
        return ageInt
    }
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
//                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri)
//                startActivityForResult(intent, 100)

                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, 1)
            }
        }
    }
}