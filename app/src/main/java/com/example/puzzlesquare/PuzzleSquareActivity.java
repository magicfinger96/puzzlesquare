package com.example.puzzlesquare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Activity of the game part.
 */
public class PuzzleSquareActivity extends Activity  {
    private Grid gridGame;
    private Piece[] pieces = new Piece[8];
    private int[][] solution;
    private int level;
    private boolean init =false;
    private int nbCoins;
    private int posPieceCurrentlyTouched = -1;
    private int levelMaxReached;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        setContentView(R.layout.maincontent);

        solution = new int[8][2];

        final ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    backBtn.setBackgroundResource(R.drawable.back_pressed_btn);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    goToMenu();
                    backBtn.setBackgroundResource(R.drawable.back_btn);
                }
                return true;
            }
        });

        final ImageButton reinitBtn = findViewById(R.id.reinitBtn);
        reinitBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    reinitBtn.setBackgroundResource(R.drawable.reinit_pressed_btn);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    reinitBtn.setBackgroundResource(R.drawable.reinit_btn);
                    setAllPiecesInWells();
                }
                return true;
            }
        });

        final ImageView grid= findViewById(R.id.grid);

        final ImageButton solutionBtn = findViewById(R.id.solutionBtn);

        solutionBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    solutionBtn.setBackgroundResource(R.drawable.solution_btn_pressed);

                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    solutionBtn.setBackgroundResource(R.drawable.solution_btn);
                    if(level < levelMaxReached){
                        showSolution();
                    } else if(nbCoins > 0) {
                        showSolution();
                        nbCoins--;
                        setCoin(nbCoins);
                        levelMaxReached++;
                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                updateDB();
                            }
                        });
                        t.start();
                        try {
                            t.join();
                        }catch(java.lang.InterruptedException e){
                            System.exit(0);
                        }
                        switchLevel();
                    }
                }
                return true;
            }
        });

        grid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!init) {
                    solutionBtn.setY(grid.getY() + grid.getMeasuredHeight()-(Grid.FACTOR_PADDING_Y *grid.getMeasuredHeight())-solutionBtn.getMeasuredHeight());

                    gridGame = new Grid(findViewById(R.id.gridLayout),findViewById(R.id.gridContent),findViewById(R.id.grid));
                    level = Integer.valueOf(""+getIntent().getStringExtra("lvl"));
                    nbCoins = Integer.valueOf(""+getIntent().getStringExtra("nbCoins"));
                    levelMaxReached = Integer.valueOf(""+getIntent().getStringExtra("levelMaxReached"));
                    setCoin(nbCoins);
                    loadLevel(level);

                    initPiecesInWells();
                    init=true;
                }
            }
        });
    }

    public void updateDB(){
        OutputStreamWriter outputStreamWriter = null;
        try {

            outputStreamWriter = new OutputStreamWriter(openFileOutput("save.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(levelMaxReached+"\n"+nbCoins);

        } catch(IOException e){
            System.exit(0);
            return;
        } finally{
            if(outputStreamWriter != null){
                try {
                    outputStreamWriter.close();
                }catch(IOException e){
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Go the menu.
     */
    public void goToMenu(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    /**
     * Show the solution of the level.
     */
    protected void showSolution(){
        int i=0;
        for(Piece piece: pieces){
            Cell cell = gridGame.getCell(solution[i][0],solution[i][1]);
            piece.changeSize(Piece.SIZE_SQUARE);
            piece.moveImWithOrigin(cell.getX(),cell.getY());
            piece.getPreview().changeSize(Piece.SIZE_SQUARE);
            piece.getPreview().moveImWithOrigin(cell.getX(),cell.getY());
            for(ImageView im : piece.getImages()){
               int pos[] = gridGame.getXYfromPosition(new float[]{im.getX(),im.getY()});
               gridGame.getCell(pos[1],pos[0]).setIdPiece(piece.getId());
            }
            i++;
        }
    }

    /**
     * Set the coin on the screen.
     * @param coin coin of the player available.
     */
    protected void setCoin(int coin){
        TextView nbC = findViewById(R.id.nbCoin);
        nbC.setText(""+coin);
    }

    /**
     * Set all the pieces in the wells.
     */
    protected void setAllPiecesInWells(){
        for(Piece piece: pieces){
            piece.putInWell();
            piece.getPreview().putInWell();
        }
        gridGame.cleanAll();
    }

    /**
     * Initialize all the pieces in the wells.
     */
    protected void initPiecesInWells(){

        GridLayout wellLayout = findViewById(R.id.wellsLayout);
        int i =1;
        for(Piece piece: pieces){
            ImageView well = (ImageView) wellLayout.getChildAt(i);
            float xStart = well.getX()+wellLayout.getX();
            float yStart = wellLayout.getY()+well.getY();
            float width = well.getMeasuredWidth();
            float height = well.getMeasuredHeight();

            while(width <= piece.getMyWidth() || height <= piece.getMyHeight()){
                piece.changeSize((int)(piece.getSizeSquare()/1.5));
            }
            piece.setSizeInWell(piece.getSizeSquare());
            float finalX = (width-piece.getMyWidth())/2f;
            float finalY = (height-piece.getMyHeight())/2f;
            piece.moveImWithOrigin(xStart+finalX,yStart+finalY);
            piece.setPositionInWell(xStart+finalX,yStart+finalY);

            piece.getPreview().setSizeInWell(piece.getSizeSquare());
            piece.getPreview().setPositionInWell(xStart+finalX,yStart+finalY);
            piece.getPreview().putInWell();

            i++;
        }
    }


    /**
     * Load the level.
     */
    protected void loadLevel(int lvl){
        LevelHolder lvlHolder = MenuActivity.levelHolders.get(lvl-1);
        RelativeLayout linearLayout= findViewById(R.id.listPieces);
        if(linearLayout != null) {
            TextView text= findViewById(R.id.level);
            text.setText("Niveau "+lvl);
            List<int[]> disabled = lvlHolder.getPositionDisabledCells();
            for(int i=0;i<disabled.size();i++){
                ImageView im = new ImageView(this);
                gridGame.setDisabledCell(disabled.get(i)[0],disabled.get(i)[1],im);
                linearLayout.addView(im);
            }
            List<PieceHolder> piecesHolder = lvlHolder.getPieces();
            for(int i=0;i<8;i++){
                pieces[i] = new Piece(piecesHolder.get(i).getId(),new float[]{2,100}, MyColor.values()[piecesHolder.get(i).getId()]);
                int j=0;
                for(int[] posIm : (List<int[]>)(piecesHolder.get(i).getPositionSquares())){
                    ImageView im = new ImageView(this);
                    pieces[i].addImageView(im, posIm[1], posIm[0]);
                    im.setOnTouchListener(new SquareListener(im,i,j));
                    linearLayout.addView(im);
                    im.bringToFront();
                    j++;
                }
                pieces[i].setPreviewPiece(linearLayout,this);
                solution[i] = piecesHolder.get(i).getPositionSolution();
            }
        }
    }

    /**
     * Represent the OnTouchListener of a piece's square.
     */
    private class SquareListener implements OnTouchListener{
        private boolean moving = false;
        private ImageView im;
        private int posPiece;
        private int posIm;
        private PointF downPt = new PointF();
        private PointF startPt = new PointF();

        /**
         * Constructor.
         * @param _im image we set the listener
         * @param _posPiece position of the piece in the list of pieces
         * @param _posIm position of the image in the piece's list
         */
        public SquareListener(ImageView _im, int _posPiece, int _posIm){
            im = _im;
            posPiece = _posPiece;
            posIm = _posIm;
            im.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event){
            pieces[posPiece].setBeyond();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downPt.set(event.getX(), event.getY());
                    startPt.set(im.getX(), im.getY());
                    moving = true;
                    pieces[posPiece].changeSize(Piece.SIZE_SQUARE);
                    posPieceCurrentlyTouched = posPiece;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (moving) {
                        int x = (int) (startPt.x + event.getX() - downPt.x);
                        int y = (int) (startPt.y + event.getY() - downPt.y);
                        pieces[posPiece].moveIm(x, y, posIm);

                        float midY = pieces[posPiece].getYmin() + (pieces[posPiece].getMyHeight() / 2);
                        GridLayout wellLayout = findViewById(R.id.wellsLayout);
                        if (midY >= wellLayout.getY()) {
                            pieces[posPiece].setPreviewIsInWell(true);
                            pieces[posPiece].getPreview().putInWell();
                        } else {
                            pieces[posPiece].moveImBySquare(gridGame);
                        }
                        startPt.set(im.getX(), im.getY());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    moving = false;
                    if (pieces[posPiece].previewIsInWell()) {
                        gridGame.clean(pieces[posPiece].getId());
                    }
                    pieces[posPiece].setAtPreviewPos();
                    posPieceCurrentlyTouched = -1;
                    if(gridGame.isFull()){
                        win();
                        switchLevel();
                    }
                    break;
            }
        return true;
        }

    }

    /**
     * When the level is won.
     */
    public void win(){
        if( level >= levelMaxReached) {
            nbCoins++;
            levelMaxReached++;
            setCoin(nbCoins);
            Thread t = new Thread(new Runnable() {
                public void run() {
                    updateDB();
                }
            });
            t.start();
            try {
                t.join();
            }catch(java.lang.InterruptedException e){
                System.exit(0);
            }
        }
    }

    public void switchLevel(){
        if(level+1 <= MenuActivity.levelHolders.size()) {
            Intent intent = new Intent(this, PuzzleSquareActivity.class);
            intent.putExtra("lvl", "" + (level + 1));
            intent.putExtra("nbCoins", "" + nbCoins);
            intent.putExtra("levelMaxReached", "" + levelMaxReached);
            startActivity(intent);
        } else {
            goToMenu();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(posPieceCurrentlyTouched != -1){
            pieces[posPieceCurrentlyTouched].setAtPreviewPos();
        }
    }

    @Override
    public void onBackPressed() {
        goToMenu();
    }
}
