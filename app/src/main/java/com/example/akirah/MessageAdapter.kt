package com.example.akirah

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

private lateinit var userRecyclerView: RecyclerView
private lateinit var userList:ArrayList<User>
private lateinit var adapter: UserAdapter
private lateinit var mAuth: FirebaseAuth
private lateinit var mDbRef: DatabaseReference
private lateinit var currentuser: FirebaseUser
private lateinit var calendar: java.util.Calendar
private lateinit var cui:String
class MessageAdapter(val context:Context,val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE=1
    val ITEM_SENT=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType==1){
            val view: View= LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            return ReceiveViewHolder(view)
        }
        else{
            val view: View= LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
            return SentViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage=messageList[position]
        if(holder.javaClass==SentViewHolder::class.java){

            val viewHolder=holder as SentViewHolder
            holder.sentMessage.text=currentMessage.message
            holder.time1.text=currentMessage.currenttime

        }
        else{
            val viewHolder=holder as ReceiveViewHolder
            holder.receiveMessage.text=currentMessage.message
            holder.time2.text=currentMessage.currenttime


        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage=messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT;
        }
        else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
    class SentViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
           val sentMessage=itemView.findViewById<TextView>(R.id.txt_sent_message)
           val time1=itemView.findViewById<TextView>(R.id.txt_time1)
          // val status1=itemView.findViewById<TextView>(R.id.txt_status)
    }
    class ReceiveViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
         val receiveMessage=itemView.findViewById<TextView>(R.id.txt_recieve_message)
         val time2=itemView.findViewById<TextView>(R.id.txt_time2)
         //val status2=itemView.findViewById<TextView>(R.id.txt_status)
    }


}