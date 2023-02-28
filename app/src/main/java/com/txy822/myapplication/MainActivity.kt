package com.txy822.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.txy822.myapplication.databinding.ActivityMainBinding
import com.txy822.myapplication.model.BoardSize
import com.txy822.myapplication.model.MemoryCard
import com.txy822.myapplication.model.MemoryGame
import com.txy822.myapplication.util.DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG ="MainActivity"
    }

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var memoryGame: MemoryGame
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumPairs: TextView
    private lateinit var tvNumMoves: TextView

    private lateinit var clRoot: ConstraintLayout


    private var boardSize : BoardSize =BoardSize.EASY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
       // setContentView(binding.root)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves =findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        clRoot = findViewById(R.id.clRoot)

        val chosenImages=DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomisedImages = (chosenImages+ chosenImages).shuffled()
        val memoryCards = randomisedImages.map { MemoryCard(it, false, false) }

        memoryGame= MemoryGame(boardSize)
       adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object : MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
                Log.i(TAG, " Card Clicked $position")
            }

        })
        rvBoard.adapter= adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    private fun updateGameWithFlip(position: Int) {

        if(memoryGame.haveWonGame()){
            Snackbar.make(clRoot, "You already won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)){
            Snackbar.make(clRoot, "Invalid Move!", Snackbar.LENGTH_LONG).show()
            return
        }
       if( memoryGame.flipCard(position)){
           Log.i(TAG, "Found a match! Num pairs found ${memoryGame.numPairsFound}")
       }
        adapter.notifyDataSetChanged()
    }
}