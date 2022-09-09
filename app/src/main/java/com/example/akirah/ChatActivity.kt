package com.example.akirah

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalTime
import java.util.HashMap

//val textView: TextView = findViewById<TextView>(R.id.txt_typing)
class ChatActivity : AppCompatActivity() {
    //private lateinit var scrollView:ScrollView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox:EditText
    private lateinit var sendButton: ImageView
    private lateinit var cui:String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentuser: FirebaseUser
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList:ArrayList<Message>
    private lateinit var mDbRef:DatabaseReference
    private lateinit var usersDbref:DatabaseReference
    private lateinit var buttonScrollDown:ImageButton

    var receiverRoom: String?=null
    var senderRoom: String?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        mAuth= FirebaseAuth.getInstance()
        currentuser= mAuth.currentUser!!
        var textWatcher:TextWatcher
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name=intent.getStringExtra("name")
        val recieverUid=intent.getStringExtra("uid")

        val senderUid=FirebaseAuth.getInstance().currentUser?.uid
        mDbRef=FirebaseDatabase.getInstance().getReference()
        usersDbref=FirebaseDatabase.getInstance().getReference("user")
        updateUserStatus("online")
        senderRoom = senderUid + recieverUid
        receiverRoom =  recieverUid +senderUid

                this.supportActionBar?.title= name

        chatRecyclerView=findViewById(R.id.chatRecyclerView)
        messageBox=findViewById(R.id.messageBox)
        sendButton=findViewById(R.id.sentbutton)
        //scrollView=findViewById(R.id.scrollview)
        buttonScrollDown=findViewById(R.id.buttonScrollDown)
        messageList= ArrayList()
        messageAdapter= MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager=LinearLayoutManager(this)
        chatRecyclerView.adapter=messageAdapter



        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children){
                        val message=postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)

                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        usersDbref.orderByChild("uid").equalTo(recieverUid)
            .addValueEventListener(object :ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(postSnapshot in snapshot.children){
                        var typingstatus:String = postSnapshot.child("typingto").value.toString();
                        Log.i("typing", typingstatus)

                        if (typingstatus != null) {
                            val textView: TextView = findViewById<TextView>(R.id.txt_typing)
                            if(typingstatus == senderUid){
                                textView.text = "typing..."
                                mDbRef.child("chats").child(senderRoom!!).child("messages")
                                    .addValueEventListener(object :ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            messageList.clear()
                                            for(postSnapshot1 in snapshot.children){
                                                val message=postSnapshot1.getValue(Message::class.java)
                                                messageList.add(message!!)

                                            }
                                            messageAdapter.notifyDataSetChanged()
                                            chatRecyclerView.scrollToPosition(messageAdapter.itemCount-1)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                            }
                            else{
                                textView.text = "..."
                                mDbRef.child("chats").child(senderRoom!!).child("messages")
                                    .addValueEventListener(object :ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            messageList.clear()
                                            for(postSnapshot2 in snapshot.children){
                                                val message=postSnapshot2.getValue(Message::class.java)
                                                messageList.add(message!!)

                                            }
                                            messageAdapter.notifyDataSetChanged()
                                            chatRecyclerView.scrollToPosition(messageAdapter.itemCount-1)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                            }
                        }

                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        buttonScrollDown.setOnClickListener {
            chatRecyclerView.smoothScrollToPosition(0)
            //chatRecyclerView.scrollToPosition(messageAdapter.itemCount-1)
            //scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }



        sendButton.setOnClickListener{
            val message=messageBox.text.toString()
            val currenttime=LocalTime.now().toString()
            val messageObject=Message(message,senderUid,currenttime)
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject,).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.setText("")
        }
        messageBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().trim().length==0)
                    checktypingstatus("noone");
                else
                    checktypingstatus(recieverUid.toString())

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        updateUserStatus("online")
    }



    @RequiresApi(Build.VERSION_CODES.N)
    public fun updateUserStatus(state:String) {
        val saveCt:String;
        val saveCd:String;
        val cal= Calendar.getInstance()
        val cd= SimpleDateFormat("MMM dd, yyyy")
        val savecd=cd.format(cal.time)
        val ct= SimpleDateFormat("hh:mm a")
        val savect=ct.format(cal.time)

        var updatestatemap: HashMap<String, String> = hashMapOf("time" to savect,"date" to savecd,"state" to state)
        var updatemap:HashMap<String,String> = hashMapOf("state" to state,"time" to savect,"date" to savecd)
        cui=  mAuth.currentUser?.uid.toString()
        mDbRef.child("user").child(cui).child("userstate")
            .updateChildren(updatestatemap as Map<String, Any>)

        mDbRef.child("user").child(cui).updateChildren(updatemap as Map<String,Any>)


    }
    private fun checktypingstatus(typing:String) {
        var updatemap:HashMap<String,String> = hashMapOf("typingto" to typing)
        cui=  mAuth.currentUser?.uid.toString()
        mDbRef.child("user").child(cui)
            .updateChildren(updatemap as Map<String, Any>)


    }

    override fun onPause() {
        super.onPause()

        checktypingstatus("noone")
    }
}