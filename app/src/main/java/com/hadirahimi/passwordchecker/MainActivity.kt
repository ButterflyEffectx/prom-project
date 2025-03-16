package com.hadirahimi.passwordchecker

import android.util.Log
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hadirahimi.passwordchecker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private var currentPasswordStrength = ""




    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        val drawableSuccess = ContextCompat.getDrawable(this, R.drawable.ic_have)
        val drawableError = ContextCompat.getDrawable(this, R.drawable.ic_error)

        setupTextWatcher(drawableSuccess, drawableError)
        setupButtons()


    }

    private fun setupButtons() {
        binding.btnSavePassword.setOnClickListener {
            savePassword()
        }

        binding.btnViewHistory.setOnClickListener {
            val intent = Intent(this, PasswordHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnToggleLanguage.setOnClickListener {
            // Toggle language
            val context = LocaleHelper.toggleLanguage(this)

            // Recreate activity to apply new language
            recreate()
        }

        binding.btnExt.setOnClickListener {
            // แสดงไดอะล็อกยืนยันก่อนออกจากแอพ
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setMessage(R.string.confirm_exit)
                .setPositiveButton(R.string.yes) { _, _ ->
                    finish()
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }

    private fun savePassword() {
        val password = binding.etPassword.text.toString().trim()

        if (password.isEmpty()) {
            Toast.makeText(this, R.string.password_empty, Toast.LENGTH_SHORT).show()
            return
        }

        // Save password to database
        dbHelper.savePassword(password, currentPasswordStrength)
        Toast.makeText(this, R.string.password_saved, Toast.LENGTH_SHORT).show()

        // เพิ่ม Log เพื่อแสดงข้อมูลใน Logcat
        Log.d("PasswordChecker", "Password saved - Length: ${password.length}, Strength: $currentPasswordStrength")
        Log.i("PasswordChecker", "Password details - " +
                "Length: ${password.length}, " +
                "Has lowercase: ${password.any { it.isLowerCase() }}, " +
                "Has uppercase: ${password.any { it.isUpperCase() }}, " +
                "Has symbol: ${password.any { !it.isLetterOrDigit() }}, " +
                "Strength: $currentPasswordStrength")
    }

    private fun setupTextWatcher(drawableSuccess: Drawable?, drawableError: Drawable?) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                s?.let { password ->
                    if (password.isNotEmpty()) {
                        val strength = checkPasswordStrength(
                            password.toString(),
                            drawableSuccess,
                            drawableError
                        )
                        // Store current strength for saving
                        currentPasswordStrength = strength.toString()

                        // update ui
                        binding.tvResult.text = getString(R.string.password_status) + ": $strength"
                    } else {
                        resetDrawables()
                        binding.tvLength.text = "00"
                        binding.tvResult.text = ""
                        currentPasswordStrength = ""
                    }
                }
            }
        }
        binding.etPassword.addTextChangedListener(textWatcher)
    }

    private fun checkPasswordStrength(password: String, drawableSuccess: Drawable?, drawableError: Drawable?): Any {
        val length = password.length
        var hasLowerCase = false
        var hasUpperCase = false
        var hasSymbol = false

        binding.tvLength.text = String.format("%02d", length)
        for (char in password) {
            if (!hasLowerCase && !hasUpperCase && !hasSymbol) {
                resetDrawables()
            }
            if (Character.isLowerCase(char)) {
                hasLowerCase = true
                binding.hasLowerCase.setCompoundDrawablesWithIntrinsicBounds(drawableSuccess, null, null, null)
            }
            if (Character.isUpperCase(char)) {
                hasUpperCase = true
                binding.hasUpperCase.setCompoundDrawablesWithIntrinsicBounds(drawableSuccess, null, null, null)
            }
            if (!char.isLetterOrDigit()) {
                hasSymbol = true
                binding.hasSymbol.setCompoundDrawablesWithIntrinsicBounds(drawableSuccess, null, null, null)
            }
        }
        if (length < 8 || !(hasLowerCase && hasUpperCase && hasSymbol)) {
            return getString(R.string.weak)
        } else if (length < 12) {
            return getString(R.string.moderate)
        } else {
            return getString(R.string.strong)
        }
    }

    private fun resetDrawables() {
        val drawableError = ContextCompat.getDrawable(this, R.drawable.ic_error)
        binding.hasLowerCase.setCompoundDrawablesWithIntrinsicBounds(drawableError, null, null, null)
        binding.hasUpperCase.setCompoundDrawablesWithIntrinsicBounds(drawableError, null, null, null)
        binding.hasSymbol.setCompoundDrawablesWithIntrinsicBounds(drawableError, null, null, null)
    }


}