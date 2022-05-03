package com.example.firebaselogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.firebaselogin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    //    ViewBinding
    private lateinit var binding: ActivityLoginBinding

    //    ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //    FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging In...")
        progressDialog.setCanceledOnTouchOutside(false)

        // init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        // handle click, open SignUpActivity
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // handle click, begin login
        binding.loginBtn.setOnClickListener {

            // before logging in, validate data
            validateData()
        }


    }
    private fun validateData() {

        // get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        // validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            // invalid email format
            binding.emailEt.error = "Invalid email format"
        } else if (TextUtils.isEmpty(password)) {
            // no password entered
            binding.passwordEt.error = "Please enter password"
        } else {

            // data is validated, begin login
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {

        // show progress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                // login success
                progressDialog.dismiss()

                // get user info
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Logged in as $email",
                    Toast.LENGTH_LONG).show()

                // open profile
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->

                // login failed
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}",
                    Toast.LENGTH_LONG).show()
            }
    }

    private fun checkUser() {

        // if user is already logged in go to profile activity
        // get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){

            // user is already logged in
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}