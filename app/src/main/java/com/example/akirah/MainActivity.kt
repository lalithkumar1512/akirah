package com.example.akirah

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
//var state:String = "offline"
//var date:String = ""
//var time:String = ""


class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList:ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef:DatabaseReference
    private lateinit var currentuser:FirebaseUser
    private lateinit var calendar: java.util.Calendar
    private lateinit var cui:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth= FirebaseAuth.getInstance()
        currentuser= mAuth.currentUser!!
        mDbRef=FirebaseDatabase.getInstance().getReference()
        userList= ArrayList()
        adapter=UserAdapter(this,userList)

        userRecyclerView=findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager=LinearLayoutManager(this)
        userRecyclerView.adapter=adapter

        mDbRef.child("user").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
               for(postSnapshot in snapshot.children){
                   val currentUser=postSnapshot.getValue(User::class.java)
                   if(mAuth.currentUser?.uid!=currentUser?.uid) {
                       userList.add(currentUser!!)
//                       if(currentUser?.name=="lalith"){
//                            state=postSnapshot.child("userstate").child("state").getValue().toString()
//                            date=postSnapshot.child("userstate").child("date").getValue().toString()
//                            time=postSnapshot.child("userstate").child("time").getValue().toString()
//                       }
                   }
               }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.logout){
            pin=0
            updateUserStatus("offline")
            mAuth.signOut()
            //updateUserStatus("offline")
            val intent=Intent(this@MainActivity,Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        else{
            pin=0
            finish()
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        if(currentuser!=null){
            updateUserStatus("online")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        super.onDestroy()
        updateUserStatus("offline")
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        updateUserStatus("offline")
    }

//    @RequiresApi(Build.VERSION_CODES.N)
//    override fun onStop() {
//        super.onStop()
//        if(currentuser!=null){
//            updateUserStatus("offline")
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.N)
    public fun updateUserStatus(state:String) {


        val cal=Calendar.getInstance()
        val cd=SimpleDateFormat("MMM dd, yyyy")
        val savecd=cd.format(cal.time)
        val ct=SimpleDateFormat("hh:mm a")
        val savect=ct.format(cal.time)

        var updatestatemap:HashMap<String,String> = hashMapOf("time" to savect,"date" to savecd,"state" to state)
        var updatemap:HashMap<String,String> = hashMapOf("state" to state,"time" to savect,"date" to savecd)


        cui= mAuth.currentUser?.uid.toString()
        mDbRef.child("user").child(cui).child("userstate")
            .updateChildren(updatestatemap as Map<String, Any>)

            mDbRef.child("user").child(cui).updateChildren(updatemap as Map<String,Any>)

    }
}