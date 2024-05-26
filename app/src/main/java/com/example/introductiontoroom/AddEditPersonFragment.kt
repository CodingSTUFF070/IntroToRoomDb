package com.example.introductiontoroom

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.introductiontoroom.databinding.FragmentAddEditPersonBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddEditPersonFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddEditPersonBinding
    private var listener: AddEditPersonListener? = null
    private var person: Person? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as AddEditPersonListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddEditPersonBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            person = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                arguments?.getParcelable("person", Person::class.java)
            else
                arguments?.getParcelable("person")
        }
        person?.let { setExistingDataOnUi(it) }
        attachUiListener()
    }

    private fun setExistingDataOnUi(person: Person) {
        binding.personNameEt.setText(person.name)
        binding.personAgeEt.setText(person.age.toString())
        binding.personCityEt.setText(person.city)
        binding.saveBtn.text = "Update"
    }

    private fun attachUiListener() {
        binding.saveBtn.setOnClickListener {
            val name = binding.personNameEt.text.toString()
            val age = binding.personAgeEt.text.toString()
            val city = binding.personCityEt.text.toString()
            if (name.isNotEmpty() && age.isNotEmpty() && city.isNotEmpty()) {
                val person1 = Person(person?.pId ?: 0, name, age.toInt(), city)
                listener?.onSaveBtnClicked(person != null, person1)
            }
            dismiss()
        }
    }

    companion object {
        const val TAG = "AddEditPersonFragment"

        @JvmStatic
        fun newInstance(person: Person?) = AddEditPersonFragment().apply {
            arguments = Bundle().apply {
                putParcelable("person", person)
            }
        }
    }

    interface AddEditPersonListener {
        fun onSaveBtnClicked(isUpdate: Boolean, person: Person)
    }
}