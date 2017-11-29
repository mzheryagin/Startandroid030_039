package ru.startandoid.activityresult;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
  TextView tvText;
  Button btnColor;
  Button btnAlign;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tvText = (TextView)findViewById(R.id.tvText);
    btnColor = (Button)findViewById(R.id.btnColor);
    btnAlign = (Button)findViewById(R.id.btnAlign);

    btnColor.setOnClickListener(this);
    btnAlign.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {

  }
}
