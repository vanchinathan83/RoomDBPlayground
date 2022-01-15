package com.vanchi.roomdbplayground

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vanchi.roomdbplayground.databinding.ActivityMainBinding
import com.vanchi.roomdbplayground.databinding.DialogUpdateBinding
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
            val itemAdapter = ItemAdapter(employeeList,
                {
                    updateId ->
                    updateRecordDialog(updateId, employeeDao)
                },
                {
                    deleteId ->
                    deleteRecordAlertDialog(deleteId, employeeDao)
                })
            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        }else{
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }

    private fun updateRecordDialog(id:Int, employeeDao: EmployeeDao) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        val dialogBinding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(dialogBinding.root)

        lifecycleScope.launch {
            employeeDao.loadEmployeeById(id).collect {
                if(it != null) {
                    dialogBinding.etUpdateEmailId.setText(it.email)
                    dialogBinding.etUpdateName.setText(it.name)
                }
            }
        }

        dialogBinding.tvUpdate.setOnClickListener {
            lifecycleScope.launch {
                val updatedName = dialogBinding.etUpdateName.text.toString()
                val updatedEmail = dialogBinding.etUpdateEmailId.text.toString()
                if (updatedName.isNotEmpty() && updatedEmail.isNotEmpty()) {
                    employeeDao.update(EmployeeEntity(id, updatedName, updatedEmail))
                    Toast.makeText(
                        applicationContext,
                        "Updated Record Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateDialog.dismiss()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Name and Email are required for update!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        dialogBinding.tvCancel.setOnClickListener{
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    private fun deleteRecordAlertDialog(id:Int, employeeDao: EmployeeDao){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Delete Record")
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert)

        lifecycleScope.launch {
            employeeDao.loadEmployeeById(id).collect {
                if(it != null){
                    alertDialogBuilder.setMessage("Do you want to delete ${it.name}")
                }
            }
        }

        alertDialogBuilder.setPositiveButton("Yes"){ dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(applicationContext,
                    "Deleted Record Successfully!", Toast.LENGTH_SHORT).show()
            }
            dialogInterface.dismiss()
        }

        alertDialogBuilder.setNegativeButton("No"){dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


}