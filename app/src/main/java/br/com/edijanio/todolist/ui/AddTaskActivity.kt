package br.com.edijanio.todolist.ui

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import br.com.edijanio.todolist.databinding.ActivityAddTaskBinding
import br.com.edijanio.todolist.datasource.TaskDataSource
import br.com.edijanio.todolist.extensions.format
import br.com.edijanio.todolist.extensions.text
import br.com.edijanio.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity : AppCompatActivity(){

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(TASK_ID)){
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.textInputLayoutTitle.text = it.title
                binding.textInputLayoutDate.text = it.date
                binding.textInputLayoutHour.text = it.hour
                binding.textInputLayoutDescription.text = it.description
            }
        }
        insertListeners()
    }

    private fun insertListeners(){
        binding.textInputLayoutDate.editText?.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * - 1
                binding.textInputLayoutDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.textInputLayoutHour.editText?.setOnClickListener{
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.addOnPositiveButtonClickListener{
                val minute = if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
                val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                binding.textInputLayoutHour.text = "$hour:$minute"
            }
            timePicker.show(supportFragmentManager, "TIME_PICKER_TAG")
        }

        binding.btnCancelTask.setOnClickListener{
            finish()
        }

        binding.btnCreateTask.setOnClickListener{
            val task = Task(
                title = binding.textInputLayoutTitle.text,
                description = binding.textInputLayoutDescription.text,
                date = binding.textInputLayoutDate.text,
                hour = binding.textInputLayoutHour.text,
                id = intent.getIntExtra(TASK_ID, 0)
            )
            TaskDataSource.insertTask(task)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object{
        const val TASK_ID = "task_id"
    }
}