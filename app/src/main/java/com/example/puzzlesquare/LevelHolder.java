package com.example.puzzlesquare;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a level load from a file.
 */
public class LevelHolder {
    private int level;

    private List<int[]> positionDisabledCells;
    private List<PieceHolder> pieces;


    /**
     * Constructor.
     */
    public LevelHolder(int _level){
        positionDisabledCells = new ArrayList<>();
        pieces = new ArrayList<>();
        level = _level;
    }

    /**
     * Append the position of a disabled cell.
     * @param i i position
     * @param j j position
     */
    public void appendPositionDisabledCells(int i, int j){
        positionDisabledCells.add(new int[]{i,j});
    }

    /**
     * Get the position of the disabled cells.
     * @return
     */
    public List getPositionDisabledCells(){
        return positionDisabledCells;
    }


    /**
     * Set the number of the level.
     * @param _level new number of the level
     */
    public void setLevel(int _level){
        level = _level;
    }

    /**
     * Add a piece in the level.
     *
     * @param piece piece added
     */
    public void appendPiece(PieceHolder piece){
        pieces.add(piece);
    }

    /**
     * Return the pieceHolder at position i.
     * @param i ieme position in the list of the pieceHolder
     * @return the pieceHolder
     */
    public PieceHolder getPiece(int i){
        return pieces.get(i);
    }

    /**
     * Return all the pieces holder of the level.
     * @return
     */
    public List getPieces(){
        return pieces;
    }

    /**
     * Get the number of the level.
     * @return
     */
    public int getLevel(){
        return level;
    }
}
