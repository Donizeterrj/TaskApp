package com.br.donizete.taskapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.br.donizete.taskapp.R
import com.br.donizete.taskapp.databinding.FragmentFormTaskBinding
import com.br.donizete.taskapp.helper.FirebaseHelper
import com.br.donizete.taskapp.model.Task

class FormTaskFragment : Fragment() {

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var newTask: Boolean = true
    private var statusTask: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListiners()
    }


    private fun initListiners() {
        binding.btnSave.setOnClickListener { validateData() }

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            statusTask = when (id) {
                R.id.rbTodo -> 0
                R.id.rbDoing -> 1
                else -> 2
            }
        }
    }

    private fun validateData() {
        val description = binding.edtDescription.text.toString().trim()

        if (description.isNotEmpty()) {
            binding.progressBar.isVisible = true

            if (newTask) task = Task()

            task.description = description
            task.status = statusTask

            saveTask()
        } else {
            Toast.makeText(requireContext(),
                "Informe uma descrição para a tarefa",
                Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveTask() {
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
            if (task.isSuccessful){
                if(newTask){// Nova tarefa
                    findNavController().popBackStack()

                    Toast.makeText(requireContext(), "Tarefa salva com sucesso.", Toast.LENGTH_SHORT).show()
                }else{ //Editando tarefa
                binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), "Tarefa atualizada com sucesso.", Toast.LENGTH_SHORT).show()

                }
            }else{

                Toast.makeText(requireContext(), "Erro ao salvar tarefa.", Toast.LENGTH_SHORT).show()
            }
            }.addOnFailureListener {

            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
