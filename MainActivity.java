package com.example.parsing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.example.parsing.data.AnswerLyncInterface;
import com.example.parsing.data.questionBank;
import com.example.parsing.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button trueButton;
    private Button falseButton;
    private ImageButton next;
    private ImageButton previous;
    private TextView question;
    private TextView scoreText;
    private TextView questionNumber;
    private TextView highestScore;
    private int count=0;
    private int score=0;
    private Pref pref;
    private List<Question> questionList;
    RequestQueue Queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        questionList=new questionBank().getQuestions(new AnswerLyncInterface() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                question.setText(questionArrayList.get(count).getAnswer());
                Log.d("stuff", "processFinished: "+questionArrayList);
            }
        });
        trueButton=findViewById(R.id.true_text);
        falseButton=findViewById(R.id.false_text);
        next=findViewById(R.id.next_text);
        previous=findViewById(R.id.previous_text);
        question=findViewById(R.id.question);
        scoreText=findViewById(R.id.score);
        questionNumber=findViewById(R.id.number);
        scoreText.setText(String.valueOf("Score= "+score));
        highestScore=findViewById(R.id.highestScore);
        pref=new Pref(MainActivity.this);

        highestScore.setText(String.valueOf("Highest Score= "+pref.getHighScore()));
        questionNumber.setText(String.valueOf(count+1)+"/"+"913");

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.true_text:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_text:
                checkAnswer(false);
                updateQuestion();
                break;
            case R.id.next_text:
                count=(count+1)%questionList.size();
                questionNumber.setText(String.valueOf(count+1)+"/"+String.valueOf(questionList.size()));
                updateQuestion();
                break;
            case R.id.previous_text:
                if(count>0){
                    count=(count-1)%questionList.size();
                    questionNumber.setText(String.valueOf(count+1)+"/"+String.valueOf(questionList.size()));
                    updateQuestion();
                }
                break;

        }
    }
    public void updateQuestion(){
        question.setText(questionList.get(count).getAnswer());
    }
    public void checkAnswer(boolean value){
        boolean getResult= questionList.get(count).isAnswerTrue();
        if(value==getResult){
            score++;
            fadeAnimation();
            Toast.makeText(MainActivity.this,"That is correct",Toast.LENGTH_SHORT).show();
        }
        if(value!=getResult){
            if(score>0) {
                score--;}
                shakeAnimation();
            Toast.makeText(MainActivity.this,"That is incorrect",Toast.LENGTH_SHORT).show();
        }
        if(score>0){
        scoreText.setText(String.valueOf("Score= "+score));
        pref.saveHighScore(score);
    }
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView cardView=findViewById(R.id.cardView);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
                trueButton.setEnabled(false);
                falseButton.setEnabled(false);
            }
            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                count=(count+1)%questionList.size();
                questionNumber.setText(String.valueOf(count+1)+"/"+String.valueOf(questionList.size()));
                updateQuestion();
                trueButton.setEnabled(true);
                falseButton.setEnabled(true);
            }
            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

            }
        });
    }
    private void fadeAnimation(){
        final CardView cardView=findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation= new AlphaAnimation(1.0f,0.0f);

        alphaAnimation.setDuration(500);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
                trueButton.setEnabled(false);
                falseButton.setEnabled(false);
            }
            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                count=(count+1)%questionList.size();
                questionNumber.setText(String.valueOf(count+1)+"/"+String.valueOf(questionList.size()));
                updateQuestion();
                trueButton.setEnabled(true);
                falseButton.setEnabled(true);
            }
            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
            }
        });
    }

    @Override
    protected void onPause() {
        pref.saveHighScore(score);
        super.onPause();
    }
}
