package com.vanchi.roomdbplayground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanchi.roomdbplayground.databinding.ActivityMainBinding
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding?.btnAdd?.setOnClickListener{
            addRecord(employeeDao)
        }
        setContentView(binding?.root)
        lifecycleScope.launch{
            employeeDao.loadAllEmployees().collect{ it ->
                val list = ArrayList(it)
                setUpDateInRecyclerView(list, employeeDao)
            }
        }
    }

    fun addRecord(employeeDao: EmployeeDao){
        var name = binding?.etName?.text.toString()
        var email = binding?.etEmailAddress?.text.toString()
        if(name.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch{
                var employee = EmployeeEntity(name=name, email=email)
                employeeDao.insert(employee)
                binding?.etName?.text?.clear()
                binding?.etEmailAddress?.text?.clear()
            }
        }else {
            Toast.makeText(applicationContext,"Please fill the fields", Toast.LENGTH_SHORT).show()
        }
    }

    fun setUpDateInRecyclerView(employeeList: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao){
        if(employeeList.isNotEmpty()){
            val itemAdapter = ItemAdapter(employeeList)
            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        }else{
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }
}