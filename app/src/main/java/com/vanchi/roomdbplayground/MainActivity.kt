package com.vanchi.roomdbplayground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.vanchi.roomdbplayground.databinding.ActivityMainBinding
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
}