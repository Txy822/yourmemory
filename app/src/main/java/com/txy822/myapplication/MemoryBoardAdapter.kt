package com.txy822.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MemoryBoardAdapter(private  val context: Context, private val numPieces: Int): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       val view=  LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        return  ViewHolder(view)

    }

    override fun getItemCount() = numPieces

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(position: Int){
            //no-op
        }
    }
}

private fun RecyclerView.ViewHolder.bind(position: Int) {

}
