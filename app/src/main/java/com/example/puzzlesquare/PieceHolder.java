package com.example.puzzlesquare;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent all data of a piece in a file.
 */
public class PieceHolder {
    private int id;
    private List<int[]> positionSquares;
    private int[] positionSolution;

    /**
     * Constructor.
     * @param _id id of the piece
     */
    public PieceHolder(int _id){
        id = _id;
        positionSquares = new ArrayList<>();
        positionSolution = new int[2];
    }

    /**
     * Set the id of the piece.
     * @param _id id of the piece
     */
    public void setId(int _id){
        id = _id;
    }

    /**
     * Return the id of the piece.
     */
    public int getId(){
        return id;
    }

    /**
     * Append a square position in the list.
     * @param i i position of the square
     * @param j j position of the square
     */
    public void appendPositionSquare(int i,int j){
        positionSquares.add(new int[]{i,j});
    }

    /**
     * Return the positions of the piece's squares.
     * @return
     */
    public List getPositionSquares(){
        return positionSquares;
    }

    /**
     * Set the position of the piece's solution.
     * @param i i position of the solution
     * @param j j position of the solution
     */
    public void setPositionSolution(int i,int j){
        positionSolution[0] = i;
        positionSolution[1] = j;
    }

    public int[] getPositionSolution(){
        return positionSolution;
    }

}
