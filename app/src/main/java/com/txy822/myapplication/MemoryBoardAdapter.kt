package com.txy822.myapplication

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.txy822.myapplication.model.BoardSize
import kotlin.math.min
class MemoryBoardAdapter(private val context: Context, private val boardSize: BoardSize): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        private const val MARGIN_SIZE =10
        private const val TAG ="MemoryBoardAdapter"
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val cardWidth = parent.width / boardSize.getWidth() - (2*MARGIN_SIZE)
        val cardHeight = parent.height/boardSize.getHeight() - (2*MARGIN_SIZE)
        val cardSizeLength = min(cardWidth, cardHeight)
        val view=  LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.height= cardSizeLength
        layoutParams.height = cardSizeLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return  ViewHolder(view)

    }

    override fun getItemCount() = boardSize.numCards

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val imageButton =itemView.findViewById<ImageButton>(R.id.imageButton)
        fun bind(position: Int){
            //no-op
        }
    }
    private fun RecyclerView.ViewHolder.bind(position: Int) {
        val imageButton =itemView.findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener {
            Log.i(TAG, "Clicked on position $position")
        }

    }
}




/*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tes.eat.anywhere.roommanager.R
import com.tes.eat.anywhere.roommanager.model.data.people.People
import com.tes.eat.anywhere.roommanager.model.data.people.PeopleItem
import com.tes.eat.anywhere.roommanager.databinding.ItemPersonBinding

class PeopleAdapter(
    private val persons: People,
    val clickListner: (PeopleItem) -> Unit

) : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //private val binding = ItemPersonBinding.bind(itemView)
        private val binding = ItemPersonBinding.bind(itemView)

        val rootItem=binding.cItemView
        fun setupUI(fact: PeopleItem) {
            //binding.tvPersonFact.text = fact?.get(position)?.firstNameModel
            binding.tvName.text = "${fact?.firstNameModel} ${fact.lastNameModel}"
            binding.tvJob.text = "${fact?.jobtitleModel}"

            Glide.with(itemView.context)
                .load(fact.avatarModel)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivUserImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleAdapter.PeopleViewHolder {
        return PeopleAdapter.PeopleViewHolder(
            //LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_person, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PeopleAdapter.PeopleViewHolder, position: Int) {
        holder.setupUI(persons[position])
        holder.rootItem.setOnClickListener {
            clickListner.invoke(persons[position])
        }
    }

    override fun getItemCount() = persons.size
}
 */