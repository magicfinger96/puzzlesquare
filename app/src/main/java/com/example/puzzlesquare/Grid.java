package com.example.puzzlesquare;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent the grid of the game.
 */
public class Grid {
    private Cell[][] cells;
    private float posX;
    private float posY;
    private float diffBetweenCell;
    private List<ImageView> disabledCells;

    public final static int NB_CELL_X = 7;
    public final static int NB_CELL_Y = 6;
    public static int SIZE_LINE_GRID;
    public final static float FACTOR_PADDING_X = 10f / 294f; // what percentage represent padding (shadow of the grid) over the grid
    public final static float FACTOR_PADDING_Y = 10f / 255f;

    /**
     * Constructor.
     * @param gridLayout layout of the grid
     * @param gridContent view of the grid view 's parent
     * @param grid view of the grid
     */
    public Grid(View gridLayout, View gridContent,View grid){

        cells = new Cell[NB_CELL_Y][NB_CELL_X];
        disabledCells = new ArrayList<>();

        int[] img_coordinates = new int[2];
        grid.getLocationOnScreen(img_coordinates);


        posX = (grid.getMeasuredWidth() * FACTOR_PADDING_X) + img_coordinates[0];
        posY = gridLayout.getY()+ gridContent.getY()+(grid.getMeasuredHeight() * FACTOR_PADDING_Y);

        SIZE_LINE_GRID = Math.round((1f/294f)*grid.getMeasuredWidth());

        float sizeGridWithoutPadding = grid.getMeasuredWidth()* (1- FACTOR_PADDING_X - FACTOR_PADDING_X);
        float tmp = ((sizeGridWithoutPadding -  (NB_CELL_X + 1)*SIZE_LINE_GRID)/NB_CELL_X)+(2*SIZE_LINE_GRID)+SIZE_LINE_GRID/2;
        Piece.SIZE_SQUARE =  Math.round(tmp);
        diffBetweenCell = Piece.SIZE_SQUARE-SIZE_LINE_GRID-(SIZE_LINE_GRID/3);

        init();
    }

    /**
     * Initialization of the grid.
     */
    public void init(){
        float x;
        float y = posY;

        for(int i=0;i<NB_CELL_Y;i++){
            x = posX;
            for(int j=0;j<NB_CELL_X;j++){
                cells[i][j] = new Cell(x,y);
                x+=diffBetweenCell;
            }
            y+=diffBetweenCell;
        }

    }

    /**
     * Set a disabled cell on the grid.
     *
     * @param x position x in the grid of the disabled cell
     * @param y position y in the grid of the disabled cell
     * @param image image of the disabled cell
     */
    public void setDisabledCell(int x, int y, ImageView image){
        cells[x][y].setIdPiece(-1);

        image.setImageResource(R.drawable.disabled_square);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setAdjustViewBounds(true);
        image.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        image.getLayoutParams().height = Piece.SIZE_SQUARE;
        image.getLayoutParams().width = Piece.SIZE_SQUARE;
        image.setX(cells[x][y].getX());
        image.setY(cells[x][y].getY());

        disabledCells.add(image);
    }

    /**
     * Clean the grid from the piece with a given id.
     *
     * @param id id of the piece we cleaned
     */
    public void clean(int id){

        for(int i=0;i<NB_CELL_Y;i++){
            for(int j=0;j<NB_CELL_X;j++){
                if(cells[i][j].getIdPiece() == id){
                    cells[i][j].setIdPiece(-2);
                }
            }
        }
    }

    /**
     * Clean the grid from all the pieces
     */
    public void cleanAll(){

        for(int i=0;i<NB_CELL_Y;i++){
            for(int j=0;j<NB_CELL_X;j++){
                if(cells[i][j].getIdPiece() != -1){
                    cells[i][j].setIdPiece(-2);
                }
            }
        }
    }

    /**
     * Get the position X of the grid.
     * @return
     */
    public float getPosX(){
        return posX;
    }

    /**
     * Get the position y of the grid.
     * @return
     */
    public float getPosY(){
        return posY;
    }

    /**
     * Get the cell at the position X,Y
     * @param x position x of the cell
     * @param y position y of the cell
     * @return the cell at the position x,y in the grid
     */
    public Cell getCell(int x, int y){
        return cells[x][y];
    }

    /**
     * Get the right border of the grid.
     * @return
     */
    public float getMaxX(){
        return cells[0][NB_CELL_X-1].getX()+Piece.SIZE_SQUARE;
    }

    /**
     * Get the bottom border of the grid.
     * @return
     */
    public float getMaxY(){
        return cells[NB_CELL_Y-1][0].getY()+Piece.SIZE_SQUARE;
    }

    /**
     * Return the i j position of an image at position given in the grid.
     * @param position position of a square on the screen.
     * @return position of a square in the grid
     */
    public int[] getXYfromPosition(float[] position){
        int[] pos = new int[2];

        for(int i=0;i<NB_CELL_X;i++){
            if(cells[0][i].getX() == position[0]){
                pos[0]= i;
            }
        }

        for(int i=0;i<NB_CELL_Y;i++){
            if(cells[i][0].getY() == position[1]){
               pos[1]= i;
            }
        }

        return pos;
    }

    /**
     * Return true if the grid is complete.
     * @return
     */
    public boolean isFull(){
        for(int i=0;i<NB_CELL_Y;i++){
            for(int j=0;j<NB_CELL_X;j++){
                if(cells[i][j].getIdPiece() == -2){
                    return false;
                }
            }
        }
        return true;
    }

}
