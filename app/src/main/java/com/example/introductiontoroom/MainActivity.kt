package com.example.introductiontoroom

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.introductiontoroom.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AddEditPersonFragment.AddEditPersonListener,
    PersonDetailsAdapter.PersonDetailsClickListener {

    private lateinit var binding: ActivityMainBinding
    private var dao: PersonDao? = null
    private lateinit var adapter: PersonDetailsAdapter

    private lateinit var searchQueryLiveData: MutableLiveData<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        attachUiListener()
        subscribeDataStreams()
    }

    private fun subscribeDataStreams() {
        searchQueryLiveData.observe(this) { query ->
            lifecycleScope.launch {
                adapter.submitList(dao?.getSearchedData(query)?.first())
            }
        }
        lifecycleScope.launch {
            dao?.getAllData()?.collect { mList ->

                //   adapter.submitList(mList)
//                binding.searchcView.setQuery("" , false)
//                binding.searchcView.clearFocus()

                lifecycleScope.launch {
                    adapter.submitList(
                        dao?.getSearchedData(searchQueryLiveData.value ?: "")?.first()
                    )
                }
            }
        }
    }

    private fun initVars() {
        dao = AppDatabase.getDatabase(this).personDao()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PersonDetailsAdapter(this)
        binding.recyclerView.adapter = adapter
        searchQueryLiveData = MutableLiveData("")
    }

    private fun attachUiListener() {
        binding.floatingActionButton.setOnClickListener {
            showBottomSheet()
        }
        binding.searchcView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null)
                    onQueryChanged(newText)
                return true
            }

        })
    }

    private fun onQueryChanged(query: String) {
        searchQueryLiveData.postValue(query)
//        lifecycleScope.launch {
//                adapter.submitList(dao?.getSearchedData(query)?.first())
//        }
    }


    private fun showBottomSheet(person: Person? = null) {
        val bottomSheet = AddEditPersonFragment.newInstance(person)
        bottomSheet.show(supportFragmentManager, AddEditPersonFragment.TAG)
    }

    override fun onSaveBtnClicked(isUpdate: Boolean, person: Person) {

        lifecycleScope.launch(Dispatchers.IO) {

            if (isUpdate)
                dao?.updatePerson(person)
            else
                dao?.savePerson(person)
        }

    }

    override fun onEditPersonClick(person: Person) {
        showBottomSheet(person)
    }

    override fun onDeletePersonClick(person: Person) {
        lifecycleScope.launch(Dispatchers.IO) {
            // dao?.deletePerson(person)
            dao?.deletePersonById(person.pId)
        }
    }

}