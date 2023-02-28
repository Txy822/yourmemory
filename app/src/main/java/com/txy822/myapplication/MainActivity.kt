package com.txy822.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.txy822.myapplication.databinding.ActivityMainBinding
import com.txy822.myapplication.model.BoardSize
import com.txy822.myapplication.model.MemoryCard
import com.txy822.myapplication.model.MemoryGame
import com.txy822.myapplication.util.DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG ="MainActivity"
    }

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumPairs: TextView
    private lateinit var tvNumMoves: TextView

    private var boardSize : BoardSize =BoardSize.MEDIUM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
       // setContentView(binding.root)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves =findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        val chosenImages=DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomisedImages = (chosenImages+ chosenImages).shuffled()
        val memoryCards = randomisedImages.map { MemoryCard(it, false, false) }

        val  memoryGame= MemoryGame(boardSize)
        rvBoard.adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object : MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                Log.i(TAG, " Card Clicked $position")
            }

        })
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }
}