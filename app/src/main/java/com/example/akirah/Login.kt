package com.example.akirah

import android.content.Intent
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.PinnedPositions.pin
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
var pin=0
var email1:String = ""
var password1:String = ""


class Login : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.i("pin", pin.toString())
        Log.i("email1", email1)
        Log.i("password1", password1)

        supportActionBar?.hide()

        mAuth= FirebaseAuth.getInstance()
        if(pin==1){
            val intent=Intent(this@Login,MainActivity::class.java)
            finish()
            startActivity(intent)
            val email= email1
            val password= password1
            //login(email,password)
        }

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)


        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener{
            val email=edtEmail.text.toString()
            val password=edtPassword.text.toString()

            login(email,password)
            Log.i("email", email)
            Log.i("password", password)
        }

    }
    private fun login(email:String,password:String){

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent=Intent(this@Login,MainActivity::class.java)
                    email1=email
                    password1=password
                    pin=1;
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@Login,"user doesn't exist", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
