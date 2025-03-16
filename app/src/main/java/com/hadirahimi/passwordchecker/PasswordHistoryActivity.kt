package com.hadirahimi.passwordchecker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.hadirahimi.passwordchecker.databinding.ActivityPasswordHistoryBinding

class PasswordHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordHistoryBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: PasswordAdapter
    private var passwordList = ArrayList<PasswordItem>()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        setupRecyclerView()
        loadPasswords()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = PasswordAdapter(
            this,
            passwordList,
            onItemClick = { /* Optionally handle item click */ },
            onDeleteClick = { passwordItem ->
                showDeleteConfirmationDialog(passwordItem)
            }
        )

        binding.recyclerPasswords.apply {
            layoutManager = LinearLayoutManager(this@PasswordHistoryActivity)
            adapter = this@PasswordHistoryActivity.adapter
        }
    }

    private fun loadPasswords() {
        passwordList = dbHelper.getAllPasswords()
        adapter.updateData(passwordList)

        if (passwordList.isEmpty()) {
            binding.tvNoPasswords.visibility = View.VISIBLE
            binding.recyclerPasswords.visibility = View.GONE
        } else {
            binding.tvNoPasswords.visibility = View.GONE
            binding.recyclerPasswords.visibility = View.VISIBLE
        }
    }

    private fun showDeleteConfirmationDialog(passwordItem: PasswordItem) {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setMessage(R.string.confirm_delete_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                dbHelper.deletePassword(passwordItem.id)
                loadPasswords() // Refresh the list
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
}