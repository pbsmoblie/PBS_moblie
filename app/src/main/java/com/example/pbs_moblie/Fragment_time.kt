package com.example.pbs_moblie


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class Fragment_time : Fragment() {


    var database : DatabaseReference = FirebaseDatabase.getInstance().getReference("Step")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_time,container,false)

        val recyclerview = view.findViewById(R.id.recycler1) as RecyclerView



        val rankTimeList : MutableList<RankTimeData> = mutableListOf() //RankNumData형식의 list생성

        var adapter : RankTimeAdapter? = null
        //orderBychild를 이용해서 stepcount 수가 큰 수대로 정렬해서 가져옴
        database.child("timeranking").orderByChild("time").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){ //firebase에 값이 있다면
                    for(snapshot in p0.children){

                        val ranktimedata  = snapshot.getValue(RankTimeData::class.java) //파이어베이스에서 nickname과 stepcount 가져오기
                        if (ranktimedata != null) {
                            rankTimeList.add(0,ranktimedata) //list에 값 저장
                        }
                        adapter!!.notifyDataSetChanged() //리스트 변경 확인
                    }


                }else{

                }


            }

        })


        recyclerview.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        recyclerview.setHasFixedSize(true)
        adapter= RankTimeAdapter(context,rankTimeList )
        recyclerview.adapter =adapter



        return view
        // Inflate the layout for this fragment

    }


}
