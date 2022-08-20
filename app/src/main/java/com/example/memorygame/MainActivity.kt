package com.example.memorygame

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recycler:RecyclerView;
    private lateinit var adapter:CardAdapter;
    private lateinit var btnMix:Button;
    private lateinit var txtCurrentTries:TextView;
    private lateinit var txtBetterGame:TextView;

    private lateinit var prefs:SharedPreferences;

    data class LastCard(var card:Card?,var imageView: ImageView?);
    private lateinit var lastCard:LastCard;

    private var canPlay:Boolean = true;

    private var  currentTries:Int = 0;
    private var betterGame:Int = 0;

    private var missingCards:Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        btnMix = findViewById(R.id.btn_mix);
        txtCurrentTries = findViewById(R.id.txt_current_point);
        txtBetterGame = findViewById(R.id.txt_record_points);


        btnMix.setOnClickListener {

            val listCard = listOf<Card>(
                Card(0,R.drawable.volleyball),
                Card(1,R.drawable.child_care),
                Card(2,R.drawable.lamp),
                Card(3,R.drawable.planet),
                Card(4,R.drawable.supino),
                Card(5,R.drawable.cut),
                Card(6,R.drawable.build),
                Card(7,R.drawable.palette),
                Card(0,R.drawable.volleyball),
                Card(1,R.drawable.child_care),
                Card(2,R.drawable.lamp),
                Card(3,R.drawable.planet),
                Card(4,R.drawable.supino),
                Card(5,R.drawable.cut),
                Card(6,R.drawable.build),
                Card(7,R.drawable.palette),
            )



            var listCardView = listCard.shuffled()
            adapter = CardAdapter(listCardView);
            recycler.adapter = adapter;
            lastCard = LastCard(null,null);
            missingCards = listCard.lastIndex;
            currentTries = 0;
            txtCurrentTries.text = getString(R.string.current_tries,currentTries);
        }

        recycler = findViewById(R.id.rv_card_memory);
        recycler.layoutManager = GridLayoutManager(this@MainActivity,4);

        btnMix.callOnClick()

        txtCurrentTries.text = getString(R.string.current_tries,currentTries);

        prefs = getSharedPreferences("shared",0);
        betterGame = prefs.getInt("record",0);
        txtBetterGame.text = getString(R.string.better_try,betterGame);


    }
    private fun updateTries(){
        currentTries++;
        txtCurrentTries.text = getString(R.string.current_tries,currentTries);
    }
    private fun updateGame(){
        missingCards-= 2;
        if(missingCards > 0)
            return;

        if(currentTries >= betterGame && betterGame != 0)
            return;

        betterGame = currentTries;
        val edit  = prefs.edit()
        edit.putInt("record",currentTries)
        edit.commit();

        txtBetterGame.text = getString(R.string.better_try,betterGame);

    }



    private inner class CardAdapter(
        private val cardList:List<Card>
    ):RecyclerView.Adapter<CardAdapter.CardViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = layoutInflater.inflate(R.layout.card_layout,parent,false);
            return CardViewHolder(view)
        }

        override fun onBindViewHolder(currentHolder: CardViewHolder, position: Int) {
            val currentItem = cardList[position];
            currentHolder.bind(currentItem);
        }

        override fun getItemCount(): Int {
            return  cardList.size
        }
        private inner class CardViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
            fun bind(card:Card){
                val imgCard:ImageView = itemView.findViewById(R.id.img_card)
                imgCard.setImageResource(card.imgCard);


                imgCard.visibility = View.INVISIBLE;

                val view = itemView as LinearLayout;
                view.setOnClickListener{
                    if (card.upTurned || !canPlay){

                        return@setOnClickListener;
                    }

                    imgCard.visibility = View.VISIBLE;
                    card.upTurned = true;

                    if(lastCard.card == null){


                        lastCard.card = card;
                        lastCard.imageView = imgCard;
                        return@setOnClickListener;
                    }
                    if(lastCard.card!!.id == card.id){

                        updateTries();
                        updateGame();
                        lastCard.card = null;
                        lastCard.imageView = null;
                        return@setOnClickListener;
                    }
                    canPlay = false;
                    Handler().postDelayed({

                        card.upTurned =  false;
                        imgCard.visibility = View.INVISIBLE;

                        lastCard.card!!.upTurned = false;
                        lastCard.imageView!!.visibility = View.INVISIBLE;
                        lastCard.card = null;
                        lastCard.imageView = null;

                        canPlay = true;
                    }, 1000)
                    updateTries();

                    Log.i("teste","DIFERENTES")

                }

            }
        }
    }



}