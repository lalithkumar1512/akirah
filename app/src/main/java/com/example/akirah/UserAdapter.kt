package com.example.akirah

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

private lateinit var mAuth: FirebaseAuth
private lateinit var mDbRef: DatabaseReference
private lateinit var currentuser: FirebaseUser
private lateinit var calendar: java.util.Calendar
private lateinit var cui:String

class  UserAdapter(val context: Context,val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View= LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
       val currentUser=userList[position]
        mDbRef= FirebaseDatabase.getInstance().getReference()
        cui= currentUser.uid.toString()
        val state =mDbRef.child("user").child(cui).child("userstate").child("state").get()
        if(currentUser.state=="online") {
            holder.textName.text = currentUser.name + "\n" + currentUser.state
        }
        else{
            holder.textName.text = currentUser.name + "\nlast seen:" + currentUser.time+"\n"+currentUser.date
        }
        holder.itemView.setOnClickListener{
            val intent =Intent(context,ChatActivity::class.java)

            intent.putExtra("name",currentUser.name)
            intent.putExtra("uid",currentUser.uid)



            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
           val textName=itemView.findViewById<TextView>(R.id.txt_name)

    }

}