package com.example.puzzlesquare;

/**
 * Represent a cell in the grid of the game.
 */
public class Cell {
    private float x;
    private float y;
    private int idPiece; // =-2 if there is no piece on the cell, -1 if this is a disabled cell

    /**
     * Constructor.
     *
     * @param _x position i in the grid
     * @param _y position j in the grid
     */
    public Cell(float _x, float _y){
        x=_x;
        y=_y;
        idPiece = -2;
    }

    /**
     * Set the id of the piece occupying the cell.
     * @param _id id of the piece, if empty: -2, if disabled cell: -1
     */
    public void setIdPiece(int _id){
        idPiece = _id;
    }

    /**
     * Return the id of the piece occupying the cell.
     * @return
     */
    public int getIdPiece(){
        return idPiece;
    }

    /**
     * Return the i position of the cell in the grid.
     * @return
     */
    public float getX(){
        return x;
    }

    /**
     * Return the j position of the cell in the grid.
     * @return
     */
    public float getY(){
        return y;
    }
}
