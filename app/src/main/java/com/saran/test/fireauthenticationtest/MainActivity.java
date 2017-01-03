package com.saran.test.fireauthenticationtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button emailbtn,googlebtn,fbbtn,twitterbtn,gitbtn;
    private Intent intent;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailbtn = (Button)findViewById(R.id.mailsign);
        googlebtn = (Button)findViewById(R.id.googlesign);
        fbbtn = (Button)findViewById(R.id.fbsign);
        twitterbtn = (Button)findViewById(R.id.twittersign);
        gitbtn = (Button)findViewById(R.id.gitsign);

        intent = new Intent(this,SigninActivity.class);

        emailbtn.setOnClickListener(this);
        googlebtn.setOnClickListener(this);
        fbbtn.setOnClickListener(this);
        twitterbtn.setOnClickListener(this);
        gitbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == emailbtn.getId()){
            setActivity(R.string.emailsgn);
        }else if(view.getId() == googlebtn.getId()){
            setActivity(R.string.google);
        }else if(view.getId() == fbbtn.getId()){
            setActivity(R.string.facebook);
        }else if(view.getId() == twitterbtn.getId()){
            setActivity(R.string.twitter);
        }else if(view.getId() == gitbtn.getId()){
            setActivity(R.string.git);
        }
    }

    public void setActivity(int type){
        extras = new Bundle();
        extras.putInt("Type",type);
        intent.putExtras(extras);
        MainActivity.this.startActivity(intent);
    }

}
