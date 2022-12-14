package com.example.akirah

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef:DatabaseReference

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth= FirebaseAuth.getInstance()

        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val name=edtName.text.toString()
            val email=edtEmail.text.toString()
            val password=edtPassword.text.toString()

            signUp(name,email,password)
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun signUp(name:String, email:String, password:String){

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                    val intent=Intent(this,MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUp,"some error occured",Toast.LENGTH_SHORT).show()
                }
            }//logging
    }

    private fun updatestatus(): String {
        val user = mAuth.currentUser
        if (user != null) {
            return "online"
        } else {
            return "offline"
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun addUserToDatabase(name: String, email: String, uid:String){
        val cal= Calendar.getInstance()
        val cd= SimpleDateFormat("MMM dd, yyyy")
        val savecd=cd.format(cal.time)
        val ct= SimpleDateFormat("hh:mm a")
        val savect=ct.format(cal.time)
           mDbRef=FirebaseDatabase.getInstance().getReference()

           mDbRef.child("user").child(uid).setValue(User(name, email, uid ,"none",updatestatus(),savect,savecd))
    }

}