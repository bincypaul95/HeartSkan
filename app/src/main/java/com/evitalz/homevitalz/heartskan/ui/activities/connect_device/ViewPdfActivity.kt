package com.evitalz.homevitalz.heartskan.ui.activities.connect_device

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.FileProvider
import com.evitalz.homevitalz.heartskan.R
import com.evitalz.homevitalz.heartskan.databinding.ActivityViewPdfBinding
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.shockwave.pdfium.PdfDocument

import java.io.File

class ViewPdfActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    lateinit var binding : ActivityViewPdfBinding
    var pageNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val f = File(intent.getStringExtra("file_name"))
        binding.pdfView!!.fromFile(f)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
    }


    override fun loadComplete(nbPages: Int) {
        val meta = binding.pdfView!!.documentMeta
        printBookmarksTree(binding.pdfView!!.tableOfContents, "-")
    }

    fun printBookmarksTree(tree: List<PdfDocument.Bookmark>, sep: String) {
        for (b in tree) {
            if (b.hasChildren()) {
                printBookmarksTree(b.children, "$sep-")
            }
        }
    }

    override fun onPageError(page: Int, t: Throwable?) {
        println("@@@$page")
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.btnshare -> {
                val fileUri = FileProvider.getUriForFile(
                        this,
                        "com.evitalz.homevitalz.cardfit.provider",
                        File(intent.getStringExtra("file_name"))
                )
                sharepdf(fileUri)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sharefile, menu)
        return true

    }

    fun sharepdf(fileUri: Uri) {


        val intentShareFile = Intent(Intent.ACTION_SEND)
        if (fileUri != null) {

            intentShareFile.type = "*/*"
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri)
            intentShareFile.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "ECG Graph- carDB"
            )
            intentShareFile.putExtra(
                    Intent.EXTRA_TEXT,
                    "Dear Mam/sir, \n Please Find the attachment of ECG Graph"
            )
            startActivity(Intent.createChooser(intentShareFile, "SHARE REPORT"))
        }
    }


}