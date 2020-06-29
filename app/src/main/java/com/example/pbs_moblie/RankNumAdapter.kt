package com.example.pbs_moblie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ThrowOnExtraProperties
import org.w3c.dom.Text

//리사이클러뷰와 데이터를 연결하기 위한 어뎁터
class RankNumAdapter(val context: Context?, val RankNumList:MutableList<RankNumData>) : RecyclerView.Adapter<RankNumAdapter.CustomViewHolder>(){
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nickname = itemView.findViewById<TextView>(R.id.Nickname)
        val stepcount= itemView.findViewById<TextView>(R.id.Stepcount)

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RankNumAdapter.CustomViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return RankNumList.size
    }

    override fun onBindViewHolder(holder: RankNumAdapter.CustomViewHolder, position: Int) {
        holder.nickname.text = RankNumList.get(position).nickname
        holder.stepcount.text=RankNumList.get(position).stepcount

    }


}