package com.example.pbs_moblie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_calendar.*
import java.security.AccessController.getContext

class CalendarActivity : AppCompatActivity(){


    var database : DatabaseReference = FirebaseDatabase.getInstance().reference
    var nickname : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val intent1 = getIntent()
        nickname = intent1.getStringExtra("nickname") //intent로 받아온 닉네임을 nickname에 저장

        //날짜를 터치 할때마다 그 날 걸었던 걸음 수(파이어베이스에 저장되어있는)를 출력해줌
        calendarView.setOnDateChangeListener{view, year,month,dayOfMonth -> //달력의 날짜를 터치할 때마다

            val date :String = String.format("%d-%d-%d",year,month+1,dayOfMonth) //오늘 날짜를 년-월-일 형식으로
            database.child("Step").child(nickname).child(date).addValueEventListener( object  : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    if(p0.exists()){ //파이어베이스에 그 날짜에 맞는 값이 존재한다면
                        var stepcount = p0.child("stepcount").getValue(String::class.java)
                        //파이어베이스에 저장되었던 걸음 수를 가져옴

                        diarytextview.text = "걸음 수 :"+stepcount
                        //가져온 걸음 수를 textview에 출력
                    }else{
                        diarytextview.text = "오늘은 걷지 않았습니다~^^"
                    }



                }

            })
        }
    }
}
