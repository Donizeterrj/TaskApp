package com.br.donizete.taskapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.br.donizete.taskapp.R
import com.br.donizete.taskapp.databinding.FragmentDoingBinding
import com.br.donizete.taskapp.databinding.FragmentDoneBinding
import com.br.donizete.taskapp.helper.FirebaseHelper
import com.br.donizete.taskapp.model.Task
import com.br.donizete.taskapp.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoingFragment : Fragment() {

    private var _binding: FragmentDoingBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter

    private val taskList = mutableListOf<Task>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ):View {
        _binding = FragmentDoingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getTasks()
    }

    private fun getTasks(){
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){

                        taskList.clear()

                        for(snap in snapshot.children){
                            val task = snap.getValue(Task::class.java) as Task

                            if (task.status == 1) taskList.add(task)
                        }
                        binding.textInfo.text = ""
                        taskList.reverse()
                        initAdapter()
                    }else{
                        binding.textInfo.text = "Nenhuma tarefa cadastrada"

                    }
                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun initAdapter(){
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())

        binding.rvTask.setHasFixedSize(true)

        taskAdapter = TaskAdapter(requireContext(), taskList){ task, select ->
            optionSelect(task, select)

        }

        binding.rvTask.adapter = taskAdapter
    }

    private fun optionSelect(task: Task, select: Int){
        when(select){
            TaskAdapter.SELECT_REMOVE -> {
                deleteTask(task)
            }
        }
    }

    private fun deleteTask(task: Task){
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .removeValue()

        taskList.remove(task)
        taskAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}