package com.txy822.memorygame

import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jinatonic.confetti.CommonConfetti
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
//import com.txy822.memorygame.databinding.ActivityMainBinding
import com.txy822.memorygame.model.BoardSize
import com.txy822.memorygame.model.MemoryGame
import com.txy822.memorygame.model.UserImageList
import com.txy822.memorygame.util.EXTRA_BOARD_SIZE
import com.txy822.memorygame.util.EXTRA_GAME_NAME

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val CREATE_REQUEST_CODE = 248
    }

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var memoryGame: MemoryGame
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumPairs: TextView
    private lateinit var tvNumMoves: TextView

    private lateinit var clRoot: CoordinatorLayout
    private var customGameImages: List<String>? = null

    private val storage = Firebase.storage
    private val db = Firebase.firestore
    private var gameName: String? = null


    private var boardSize: BoardSize = BoardSize.EASY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        clRoot = findViewById(R.id.clRoot)
        setupBoard()
    }

    private fun setupBoard() {
        supportActionBar?.title = gameName ?: getString(R.string.app_name)
        when (boardSize) {
            BoardSize.EASY -> {
                tvNumMoves.text = "Easy: 4 x 2"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "MEDIUM: 6 x 3"
                tvNumPairs.text = "Pairs: 0 / 9"
            }
            BoardSize.HARD -> {

                tvNumMoves.text = "HARD: 7 x 4"
                tvNumPairs.text = "Pairs: 0 / 14"
            }
            BoardSize.TOUGH -> {
                tvNumMoves.text = "TOUGH: 8 x 5"
                tvNumPairs.text = "Pairs: 0 / 20"
            }
        }
        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize, customGameImages)
        adapter = MemoryBoardAdapter(
            this,
            boardSize,
            memoryGame.cards,
            object : MemoryBoardAdapter.CardClickListener {
                override fun onCardClicked(position: Int) {
                    updateGameWithFlip(position)
                    Log.i(TAG, " Card Clicked $position")
                }

            })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun showAlertDialog(
        title: String,
        view: View?,
        positiveButtonClickListener: View.OnClickListener
    ) {
        AlertDialog.Builder(this)
            .setTitle(title).setView(view)
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("OK") { _, _ ->
                positiveButtonClickListener.onClick(null)
            }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()) {
                    showAlertDialog(
                        "Quit your current game?",
                        null,
                        View.OnClickListener { setupBoard() })
                } else {
                    setupBoard()
                }
            }
            R.id.mi_new_size -> {
                showSizeDialog()
                return true
            }
            R.id.mi_custom -> {
                showCreationDialog()
                return true
            }
            R.id.mi_download -> {
                showDownloadDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDownloadDialog() {
        val boardDownloadView =
            LayoutInflater.from(this).inflate(R.layout.dialog_download_board, null)
        showAlertDialog("Fetch memory game", boardDownloadView, View.OnClickListener {
            val etDownloadGame = boardDownloadView.findViewById<EditText>(R.id.etDownloadGame)
            val gameToDownload = etDownloadGame.text.toString().trim()
            if(gameToDownload.isBlank()){
                Snackbar.make(
                    clRoot,
                    "Game name is empty!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            else {
                downloadGame(gameToDownload)
            }
        })
    }

    private fun showCreationDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        showAlertDialog("Create your own memory board", boardSizeView, View.OnClickListener {
            val desiredBoardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                R.id.rbHard -> BoardSize.HARD
                R.id.rbTough -> BoardSize.TOUGH
                else -> BoardSize.HARD
            }
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra(EXTRA_BOARD_SIZE, desiredBoardSize)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CREATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val customGameName = data?.getStringExtra(EXTRA_GAME_NAME)
            if (customGameName == null) {
                Log.e(TAG, " Got null custom game from CreateActivity")
                return
            }
            downloadGame(customGameName)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun downloadGame(customGameName: String) {
        db.collection("games").document(customGameName).get().addOnSuccessListener { document ->
            val userImageList = document.toObject(UserImageList::class.java)
            if (userImageList?.images == null) {
                Log.e(TAG, "Invalid custom game data from Firebase")
                Snackbar.make(
                    clRoot,
                    "Sorry, we couldn't find any such game, '$gameName'",
                    Snackbar.LENGTH_LONG
                ).show()
                return@addOnSuccessListener
            }
            val numCards = userImageList.images.size * 2
            boardSize = BoardSize.getByValue(numCards)
            customGameImages = userImageList.images
            gameName = customGameName
            // Pre-fetch the images for faster loading
            for (imageUrl in userImageList.images) {
                Picasso.get().load(imageUrl).fetch()
            }
            Snackbar.make(clRoot, "You're now playing '$customGameName'!", Snackbar.LENGTH_LONG)
                .show()
            setupBoard()
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Exception when retrieving game", exception)
        }
    }

    private fun showSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (boardSize) {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
            BoardSize.TOUGH -> radioGroupSize.check(R.id.rbTough)

        }
        showAlertDialog("Choose new size", boardSizeView, View.OnClickListener {
            boardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                R.id.rbHard -> BoardSize.HARD
                R.id.rbTough -> BoardSize.TOUGH
                else -> BoardSize.HARD
            }
            gameName = null
            customGameImages = null
            setupBoard()
        })
    }

    private fun updateGameWithFlip(position: Int) {

        if (memoryGame.haveWonGame()) {
            Snackbar.make(clRoot, "You already won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)) {
            Snackbar.make(clRoot, "Invalid Move!", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (memoryGame.flipCard(position)) {
            Log.i(TAG, "Found a match! Num pairs found ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()) {
                Snackbar.make(clRoot, " You have won! Congratulations.", Snackbar.LENGTH_LONG)
                    .show()
                confettiAnimation()
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }

    private fun confettiAnimation() {
        CommonConfetti.explosion(clRoot, 500,900,   intArrayOf(
            Color.YELLOW,
            Color.RED,
            Color.MAGENTA,
            Color.GREEN,
            Color.BLACK,
            Color.BLUE,
            Color.GRAY,
            Color.CYAN,
            Color.LTGRAY,
            Color.DKGRAY,
            Color.TRANSPARENT,
            Color.YELLOW,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.RED,
            Color.MAGENTA,
            Color.GREEN,
            Color.BLACK,
            Color.BLUE,
            Color.GRAY,
            Color.CYAN,
            Color.LTGRAY,
            Color.DKGRAY,
            Color.TRANSPARENT,
            Color.YELLOW,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.RED,
            Color.MAGENTA,
            Color.GREEN,
            Color.BLACK,
            Color.BLUE,
            Color.GRAY,
            Color.CYAN,
            Color.LTGRAY,
            Color.DKGRAY,
            Color.TRANSPARENT,
            Color.YELLOW,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.RED,
            Color.MAGENTA,
            Color.GREEN,
            Color.BLACK,
            Color.BLUE,
            Color.GRAY,
            Color.CYAN,
            Color.LTGRAY,
            Color.DKGRAY,
            Color.TRANSPARENT,
            Color.YELLOW,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.GREEN,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.BLUE,
            Color.GREEN,
            Color.GREEN,
            Color.GREEN,
            Color.GREEN,
        )).oneShot().animate()
        CommonConfetti.rainingConfetti(
            clRoot,
            intArrayOf(
                Color.YELLOW,
                Color.RED,
                Color.MAGENTA,
                Color.GREEN,
                Color.BLACK,
                Color.BLUE,
                Color.GRAY,
                Color.CYAN,
                Color.LTGRAY,
                Color.DKGRAY,
                Color.TRANSPARENT,
                Color.YELLOW,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED,
                Color.MAGENTA,
                Color.GREEN,
                Color.BLACK,
                Color.BLUE,
                Color.GRAY,
                Color.CYAN,
                Color.LTGRAY,
                Color.DKGRAY,
                Color.TRANSPARENT,
                Color.YELLOW,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED,
                Color.MAGENTA,
                Color.GREEN,
                Color.BLACK,
                Color.BLUE,
                Color.GRAY,
                Color.CYAN,
                Color.LTGRAY,
                Color.DKGRAY,
                Color.TRANSPARENT,
                Color.YELLOW,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED,
                Color.MAGENTA,
                Color.GREEN,
                Color.BLACK,
                Color.BLUE,
                Color.GRAY,
                Color.CYAN,
                Color.LTGRAY,
                Color.DKGRAY,
                Color.TRANSPARENT,
                Color.YELLOW,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.GREEN,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.BLUE,
                Color.GREEN,
                Color.GREEN,
                Color.GREEN,
                Color.GREEN,
            )
        ).oneShot().animate()   }
}