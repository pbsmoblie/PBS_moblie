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

class RankTimeAdapter(val context: Context?, val RankTimeList:MutableList<RankTimeData>) : RecyclerView.Adapter<RankTimeAdapter.CustomViewHolder>(){
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nickname = itemView.findViewById<TextView>(R.id.Nickname)
        val timecount = itemView.findViewById<TextView>(R.id.Timecount)

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RankTimeAdapter.CustomViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.time_item,parent,false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return RankTimeList.size
    }

    override fun onBindViewHolder(holder: RankTimeAdapter.CustomViewHolder, position: Int) {
        holder.nickname.text = RankTimeList.get(position).nickname
        holder.timecount.text=RankTimeList.get(position).time

    }


}