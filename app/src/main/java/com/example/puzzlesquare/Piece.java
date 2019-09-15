package com.example.puzzlesquare;

import android.app.ActionBar;
import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a piece.
 */
public class Piece {
    private int id; // id of the piece
    private float[] position; // position of the piece,  at the top left corner
    private List<int[]> positionImg; // position of the image in table form (0 1, 0 0, etc) relative to the position attribute
    private List<ImageView> images;
    private int colorIm;
    private Piece preview;
    private int currentSizeSquare;

    private float[] positionInWell;
    private int sizeInWell;
    private boolean previewIsInWell = true;

    public static int SIZE_SQUARE; // Size of a square

    /**
     * Constructor.
     * @param _id id of the piece
     * @param _position position of the piece
     * @param _color color of the piece
     */
    public Piece(int _id, float[] _position, MyColor _color){
        id = _id;
        position = _position;
        colorIm = _color.getDrawable();
        images = new ArrayList<>();
        positionImg = new ArrayList<>();
        currentSizeSquare = SIZE_SQUARE;
    }

    /**
     * Constructor for the preview Piece.
     * @param _id id of the preview
     * @param _position position of the preview
     * @param _positionImg position of the piece's images relative to 0,0
     * @param _images pieces's images
     * @param _colorIm color of the preview
     * @param _currentSizeSquare size of the preview' image
     */
    public Piece(int _id, float[] _position, List<int[]> _positionImg, List<ImageView> _images, int _colorIm, int _currentSizeSquare){
        id = _id;
        position = _position;
        positionImg = _positionImg;
        images = _images;
        colorIm = _colorIm;
        currentSizeSquare =_currentSizeSquare;
    }

    /**
     * Set the size which take an image when it's in the well.
     * @param size
     */
    public void setSizeInWell(int size){
        sizeInWell = size;
    }

    /**
     * Set the position of the piece when it's in the well.
     * @param x
     * @param y
     */
    public void setPositionInWell(float x,float y){
        positionInWell = new float[]{x,y};
    }

    /**
     * Set the piece in the well
     */
    public void putInWell(){
        changeSize(sizeInWell);
        moveImWithOrigin(positionInWell[0],positionInWell[1]);
        previewIsInWell = true;
    }

    /**
     * Move the piece to the x,y position with its origin (the x min and y min of the piece).
     * @param x position x where to place the piece
     * @param y position y where to place the piece
     */
    public void moveImWithOrigin(float x, float y){
        float gapX = x-getCoordOrigin()[0];
        float gapY = y-getCoordOrigin()[1];
        for(int j=0;j<images.size();j++){
            images.get(j).setX(images.get(j).getX()+gapX);
            images.get(j).setY(images.get(j).getY()+gapY);
        }
    }

    /**
     * Move the i eme image at the position x,y and make the others images follow.
     * @param x new position X of the ieme image
     * @param y new position Y of the ieme image
     * @param i ieme image in the list of the piece
     */
    public void moveIm(float x, float y, int i){
        float gapX = x-images.get(i).getX();
        float gapY = y-images.get(i).getY();
        images.get(i).setX(x);
        images.get(i).setY(y);
        for(int j=0;j<images.size();j++){
            if(j != i){
                images.get(j).setX(images.get(j).getX()+gapX);
                images.get(j).setY(images.get(j).getY()+gapY);
            }
        }

        if(!images.isEmpty()) {
            position[0] = images.get(0).getX();
            position[1] = images.get(0).getY();
        }

    }

    /**
     *  Allow to set the piece above everything else.
     */
    public void setBeyond(){
        for(ImageView im: images){
            im.bringToFront();
        }
    }

    /**
     * Tells if the piece is fully in the grid.
     * @param grid grid of the game
     * @return true if the piece is fully in the grid
     */
    public boolean isFullyInGrid(Grid grid){
        return getXmin() >= grid.getPosX() && getYmin() >= grid.getPosY() &&  getXmax() <= grid.getMaxX() && getYmax() <= grid.getMaxY();
    }

    /**
     * Tells if the piece is on an other piece already set on the grid or disabled cell.
     * @param grid grid of the game
     * @return true if there is already a piece on the cell we want to put the piece
     */
    public boolean isAboveAnotherPiece(Grid grid){

        for(ImageView im : getImages()){
            int posTmp[] = grid.getXYfromPosition(new float[]{im.getX(),im.getY()});
            int idGrid = grid.getCell(posTmp[1],posTmp[0]).getIdPiece();
            if(id != idGrid && idGrid != -2){
                return true;
            }
        }
        return false;
    }

    /**
     * Move the piece square by square. (Used for the preview).
     * @param grid grid of the game
     */
    public void moveImBySquare(Grid grid){
        int posX = (int) (position[0] - grid.getPosX()) / SIZE_SQUARE;
        int posY = (int) (position[1] - grid.getPosY()) / SIZE_SQUARE;

        if(posX+1 < Grid.NB_CELL_X && posY+1 < Grid.NB_CELL_Y && position[0] >= grid.getPosX() && position[1] >= grid.getPosY()) {
            preview.changeSize(SIZE_SQUARE);
            float midX = grid.getCell(posY, posX).getX() + (grid.getCell(posY + 1, posX).getY() - grid.getCell(posY, posX).getY()) / 2;
            float midY = grid.getCell(posY, posX).getY() + (grid.getCell(posY, posX + 1).getX() - grid.getCell(posY, posX).getX()) / 2;

            if (position[0] > midX) {
                posX += 1;
            }
            if (position[1] > midY) {
                posY += 1;
            }
            float[] savePosition = new float[]{preview.getPosition()[0],preview.getPosition()[1]};
            preview.moveIm(grid.getCell(posY, posX).getX(), grid.getCell(posY, posX).getY(), 0);
            if(!preview.isFullyInGrid(grid) || preview.isAboveAnotherPiece(grid)){
                if(previewIsInWell){
                    preview.putInWell();
                } else {
                    preview.moveIm(savePosition[0],savePosition[1],0);
                }
            } else {
                previewIsInWell = false;
                grid.clean(id);
                for(ImageView im : preview.getImages()){
                    int posTmp[] = grid.getXYfromPosition(new float[]{im.getX(),im.getY()});
                    grid.getCell(posTmp[1],posTmp[0]).setIdPiece(id);
                }

            }
        }

    }

    /**
     * Set if the preview is in the well or not.
     * @param state state of the preview
     */
    public void setPreviewIsInWell(boolean state){
        previewIsInWell = state;
    }

    /**
     * Get the preview of the piece.
     * @return the preview of the piece
     */
    public Piece getPreview(){
        return preview;
    }

    /**
     * Get the position of the piece's images.
     * @return the position of the piece's images
     */
    public List<int[]> getPositionImg(){
        return positionImg;
    }

    /**
     * Add an ImageView in the piece
     * @param im new imageview to add
     * @param posX relative position X of the new image
     * @param posY relative position Y of the new image
     */
    public void addImageView(ImageView im, int posX, int posY){
        im.setImageResource(colorIm);
        im.setScaleType(ImageView.ScaleType.FIT_XY);
        im.setAdjustViewBounds(true);
        im.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT));
        im.getLayoutParams().height = SIZE_SQUARE;
        im.getLayoutParams().width = SIZE_SQUARE;
        im.setX(position[0]+(posX*(SIZE_SQUARE-Grid.SIZE_LINE_GRID-(Grid.SIZE_LINE_GRID/3))));
        im.setY(position[1]+(posY*(SIZE_SQUARE-Grid.SIZE_LINE_GRID-(Grid.SIZE_LINE_GRID/3))));

        // set the image at (0,0) always first in the list
        if(posX == 0 && posY == 0){
            if(!images.isEmpty()) {
                images.add(0, im);
                positionImg.add(0, new int[]{posX, posY});
            } else {
                images.add( im);
                positionImg.add(new int[]{posX, posY});
            }
        } else {
            images.add(im);
            positionImg.add(new int[]{posX, posY});
        }
    }

    /**
     * Change the size of the piece's square.
     * @param newSize new size of the squares
     */
    public void changeSize(int newSize){

        int newLineSize = Grid.SIZE_LINE_GRID;
        if(newSize != SIZE_SQUARE){
            newLineSize = (Grid.SIZE_LINE_GRID/SIZE_SQUARE)*newSize;
        }
        int i = 0;
        for(ImageView im : images) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) im.getLayoutParams();

            params.width = newSize;
            params.height = newSize;
            im.setLayoutParams(params);

            im.setX(position[0]+(positionImg.get(i)[0]*(newSize-newLineSize-(newLineSize/3))));
            im.setY(position[1]+(positionImg.get(i)[1]*(newSize-newLineSize-(newLineSize/3))));
            i++;
        }

        currentSizeSquare = newSize;

    }

    /**
     * Create the preview of the piece.
     * @param layout layout where the piece's images are set
     * @param context context of the activity
     */
    public void setPreviewPiece(RelativeLayout layout, Context context){
        List<int[]> posImPreview = new ArrayList<>(positionImg);
        List<ImageView> imagesPreview = new ArrayList<>();

        for(int i=0;i<images.size();i++) {
            ImageView im = new ImageView(context);
            im.setImageResource(colorIm);
            im.setAlpha(0.5f);
            im.setScaleType(ImageView.ScaleType.FIT_XY);
            im.setAdjustViewBounds(true);
            im.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.WRAP_CONTENT));
            im.getLayoutParams().height = SIZE_SQUARE;
            im.getLayoutParams().width = SIZE_SQUARE;
            im.setX(images.get(i).getX());
            im.setY(images.get(i).getY());
            imagesPreview.add(im);
            layout.addView(im);
        }

        preview = new Piece(id,new float[]{position[0],position[1]}, posImPreview, imagesPreview,colorIm,currentSizeSquare);
    }

    /**
     * Set the piece at the preview's position.
     */
    public void setAtPreviewPos(){
        if(!previewIsInWell) {
            moveIm(preview.getPosition()[0], preview.getPosition()[1], 0);
        } else {
            putInWell();
        }
    }

    /**
     * Return the lowest y position of the piece.
     * @return
     */
    public float getYmin(){
        float min = images.get(0).getY();
        for(int i=1;i<images.size();i++){
            if(min > images.get(i).getY()){
                min = images.get(i).getY();
            }
        }
        return min;
    }

    /**
     * Return the greatest y of the piece.
     * @return
     */
    public float getYmax(){

        float max = images.get(0).getY()+currentSizeSquare;

        for(int i=1;i<images.size();i++){

            if(max < images.get(i).getY()+currentSizeSquare){
                max = images.get(i).getY()+currentSizeSquare;
            }
        }
        return max;
    }

    /**
     * Return the greatest X of the piece.
     * @return
     */
    public float getXmax(){
        float max = images.get(0).getX()+currentSizeSquare;
        for(int i=1;i<images.size();i++){
            if(max < images.get(i).getX()+currentSizeSquare){
                max = images.get(i).getX()+currentSizeSquare;
            }
        }
        return max;
    }

    /**
     * Return the lowest X of the piece.
     * @return
     */
    public float getXmin(){
        float min = images.get(0).getX();
        for(int i=1;i<images.size();i++){
            if(min > images.get(i).getX()){
                min = images.get(i).getX();
            }
        }
        return min;
    }

    /**
     * Tells if the preview is in the well.
     * @return true if the preview is in the well
     */
    public boolean previewIsInWell(){
        return previewIsInWell;
    }

    /**
     * Get the id of the piece.
     * @return
     */
    public int getId(){
        return id;
    }

    /**
     * Get the width of the piece.
     * @return
     */
    public float getMyWidth(){
        return getXmax()-getXmin();
    }

    /**
     * Get the height of the piece
     * @return
     */
    public float getMyHeight(){
        return getYmax()-getYmin();
    }

    /**
     * Get the size of the image's squares.
     * @return
     */
    public int getSizeSquare(){
        return currentSizeSquare;
    }

    /**
     * Get the origin coords of the piece.
     * @return
     */
    public float[] getCoordOrigin(){
        return new float[]{getXmin(),getYmin()};
    }

    /**
     * Get the position of the piece.
     * @return
     */
    public float[] getPosition(){
        return position;
    }

    /**
     * Get the images of the piece.
     * @return
     */
    public List<ImageView> getImages(){
        return images;
    }

    /**
     * Set the position of the piece
     * @param _position new position of the piece
     */
    public void setPosition(float[] _position){
        position = _position;
    }

    /**
     * Get the color of the piece.
     * @return
     */
    public int getColor(){
        return colorIm;
    }
}
