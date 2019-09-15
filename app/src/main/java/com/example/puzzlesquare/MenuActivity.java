package com.example.puzzlesquare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity of the menu.
 */
public class MenuActivity extends Activity {
    public static List<LevelHolder> levelHolders;
    private int nbCoins;
    private int levelMaxReached;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        Thread t = new Thread(new Runnable() {
            public void run() {
                loadLevelFile();
                loadSaveFile();
            }
        });
        t.start();
        try {
            t.join();
        }catch(java.lang.InterruptedException e){
            System.exit(0);
        }
        int nbRows = (int) Math.ceil(levelHolders.size() / 4.0f);
        GridLayout gridLayout = findViewById(R.id.lvlsLayout);
        gridLayout.setRowCount(nbRows);

        int pos=0;
        for(int i=0;i<nbRows;i++){
            for(int j=0;j<4;j++){
                final MyBtn im = new MyBtn(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i),
                        GridLayout.spec(j, 1f));
                params.width = 0;
                im.setLayoutParams(params);

                if(levelHolders.size() <= pos){
                    break;
                } else if(pos < levelMaxReached) {
                    im.setBackgroundResource(R.drawable.btn_level);
                    im.setText("" + levelHolders.get(pos).getLevel());
                    im.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                    im.setGravity(Gravity.CENTER);
                    im.setTextSize(30f);
                    im.setTextColor(0x50000000);
                    //im.setPadding(0, 5, 0, 0);
                    im.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/signatra.ttf"));
                    final int posF = pos;
                    im.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                im.setBackgroundResource(R.drawable.btn_pressed_level);
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                im.setBackgroundResource(R.drawable.btn_level);
                                launchLevel(levelHolders.get(posF).getLevel(), nbCoins);
                            }
                            return true;
                        }
                    });
                } else {
                    im.setBackgroundResource(R.drawable.btn_level_locked);
                }
                gridLayout.addView(im);
                pos++;

            }
        }
    }

    /**
     * Launch the level clicked.
     * @param _level level we want to play
     */
    protected void launchLevel(int _level, int _nbCoins){
        Intent intent = new Intent(this, PuzzleSquareActivity.class);
        intent.putExtra("lvl",""+_level);
        intent.putExtra("nbCoins",""+_nbCoins);
        intent.putExtra("levelMaxReached",""+levelMaxReached);
        startActivity(intent);
    }

    public void loadSaveFile(){
        BufferedReader reader = null;
        try {
            InputStream iS = this.openFileInput("save.txt");
            reader = new BufferedReader(new InputStreamReader(iS));
            levelMaxReached = Integer.valueOf(reader.readLine());
            nbCoins = Integer.valueOf(reader.readLine());
        } catch(IOException e){
            System.exit(0);
            return;
        } finally{
            if(reader != null){
                try {
                    reader.close();
                }catch(IOException e){
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Load the file containing the levels.
     */
    protected void loadLevelFile(){

            levelHolders = new ArrayList<>();
            BufferedReader reader = null;
            try {
                InputStream iS = getResources().getAssets().open("levels.txt");
                reader = new BufferedReader(new InputStreamReader(iS));

                int x;
                int y;
                String lvlsStr;
                while((lvlsStr = reader.readLine()) != null){

                    String[] lvlStr = lvlsStr.split(" ");
                    int lvl = Integer.valueOf(lvlStr[1]);
                    LevelHolder levelHolder = new LevelHolder(lvl);
                    String[] posDisabled = reader.readLine().split(",");
                    for (String posD : posDisabled) {
                        x = Integer.valueOf("" + posD.charAt(0));
                        y = Integer.valueOf("" + posD.charAt(2));

                        levelHolder.appendPositionDisabledCells(x, y);
                    }
                    for (int i = 0; i < 8; i++) {
                        int id = Integer.valueOf(reader.readLine());
                        String[] posIms = reader.readLine().split(",");
                        PieceHolder pieceHolder = new PieceHolder(id);
                        int j = 0;
                        for (String posIm : posIms) {
                            x = Integer.valueOf("" + posIm.charAt(0));
                            y = Integer.valueOf("" + posIm.charAt(2));
                            pieceHolder.appendPositionSquare(x, y);
                            j++;
                        }
                        String[] posSolution = reader.readLine().split(" ");
                        pieceHolder.setPositionSolution(Integer.valueOf(posSolution[0]), Integer.valueOf(posSolution[1]));
                        levelHolder.appendPiece(pieceHolder);
                    }
                    levelHolders.add(levelHolder);
                }
            } catch(IOException e){
                System.exit(0);
                return;
            } finally{
                if(reader != null){
                    try {
                        reader.close();
                    }catch(IOException e){
                        System.exit(0);
                    }
                }
            }
    }

    /**
     * Represent the level button.
     */
    private class MyBtn extends AppCompatButton {
        public MyBtn(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int desiredWidth = 0;

            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            int width;
            int height;

            //Measure Width
            if (widthMode == MeasureSpec.EXACTLY) {
                //Must be this size
                width = widthSize;
            } else if (widthMode == MeasureSpec.AT_MOST) {
                //Can't be bigger than...
                width = Math.min(desiredWidth, widthSize);
            } else {
                //Be whatever you want
                width = desiredWidth;
            }


            int desiredHeight = width;

            //Measure Height
            if (heightMode == MeasureSpec.EXACTLY) {
                //Must be this size
                height = heightSize;
            } else if (heightMode == MeasureSpec.AT_MOST) {
                //Can't be bigger than...
                height = Math.min(desiredHeight, heightSize);
            } else {
                //Be whatever you want
                height = desiredHeight;
            }

            //MUST CALL THIS
            setMeasuredDimension(width, height);
        }
    }

    @Override
    public void onBackPressed() {
        //Do nothing
    }
}
