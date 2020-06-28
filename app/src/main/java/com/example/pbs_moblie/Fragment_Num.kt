package com.example.pbs_moblie

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_fragment__num.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Fragment_Num : Fragment() {


    var database : DatabaseReference = FirebaseDatabase.getInstance().getReference("Step")

    var currentdate : String ="" //현재 날짜
   // var recyclerview: RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =inflater.inflate(R.layout.fragment_fragment__num,container,false)

      val recyclerview = view.findViewById(R.id.recycler) as RecyclerView


        val Date: LocalDate = LocalDate.now() //현재 날짜 표시
        currentdate = Date.format(DateTimeFormatter.ofPattern("yyyy-M-dd")) //Date를 String으로 변환

        val rankNumList : MutableList<RankNumData> = mutableListOf() //RankNumData형식의 list생성

        var adapter : RankNumAdapter? = null
            //orderBychild를 이용해서 stepcount 수가 큰 수대로 정렬해서 가져옴
        database.child("ranking").orderByChild("stepcount").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){ //firebase에 값이 있다면
                    for(snapshot in p0.children){

                        val ranknumdata  = snapshot.getValue(RankNumData::class.java) //파이어베이스에서 nickname과 stepcount 가져오기
                        if (ranknumdata != null) {
                            rankNumList.add(0,ranknumdata) //list에 값 저장
                        }
                        adapter!!.notifyDataSetChanged() //리스트 변경 확인
                    }


                }else{

                }


            }

        })


        recyclerview.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        recyclerview.setHasFixedSize(true)
      adapter= RankNumAdapter(context,rankNumList )
        recyclerview.adapter =adapter



        return view
    }


}
