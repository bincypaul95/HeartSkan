package com.evitalz.homevitalz.cardfit.ui.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.evitalz.homevitalz.cardfit.R
import com.evitalz.homevitalz.cardfit.databinding.UserprofilefragmentBinding


import java.util.*

class ProfileFragment : Fragment() {

    lateinit var binding: UserprofilefragmentBinding

    private val viewModel : ProfileViewModel by lazy{ ViewModelProvider(this).get(
        ProfileViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= UserprofilefragmentBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


          viewModel.patientdetails.observe(viewLifecycleOwner, Observer {
              if(it.isNotEmpty()){
                  Log.d("profiledetails", "onViewCreated: ${it[0].hba1c}")
                  Log.d("profiledetails", "onViewCreated: ${it[0].bmi}")
                  Log.d("profiledetails", "onViewCreated: ${it[0].diabetic}")
                  Log.d("profiledetails", "onViewCreated: ${it[0].kidneydisease}")
                  Log.d("profiledetails", "onViewCreated: ${it[0].smoker}")
                  Log.d("profiledetails", "onViewCreated: ${it[0].angina}")
                  Log.d("profiledetails", "onViewCreated: ${it[0].waist}")
                  Log.d("profiledetails", "onViewCreated: ${it[0].hip}")
              }

          })

         viewModel.patientreg.observe(viewLifecycleOwner, Observer {
//             Log.d("profiledetails", "onViewCreated: ${it[0].pname}")
         })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.editprofile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnedit -> {
              Toast.makeText(context, "Edit Clicked", Toast.LENGTH_LONG).show()
            }
        }
        return true
    }



}