package com.example.wodbook.domain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wodbook.R
import com.example.wodbook.data.WOD

class WodAdapter(private var wods: List<WOD>) : RecyclerView.Adapter<WodAdapter.WodViewHolder>() {

    class WodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view_wod)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WodViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wod_item, parent, false)
        return WodViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WodViewHolder, position: Int) {
        val currentItem = wods[position]
        holder.textView.text = currentItem.notes
    }

    override fun getItemCount() = wods.size

    fun setWods(wods: List<WOD>) {
        this.wods = wods
        notifyDataSetChanged()
    }
}

