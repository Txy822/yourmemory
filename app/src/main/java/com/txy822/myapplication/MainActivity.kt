package com.txy822.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.txy822.myapplication.databinding.ActivityMainBinding
import com.txy822.myapplication.model.BoardSize

class MainActivity : AppCompatActivity() {

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumPairs: TextView
    private lateinit var tvNumMoves: TextView

    private var boardSize : BoardSize =BoardSize.HARD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
       // setContentView(binding.root)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves =findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        rvBoard.adapter = MemoryBoardAdapter(this, boardSize)
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }
}