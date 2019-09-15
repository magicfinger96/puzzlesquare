package com.example.puzzlesquare;

/**
 * Represent the color of the piece available.
 */
public enum MyColor {
    RED(R.drawable.red_square),BROWN(R.drawable.brown_square),GREEN(R.drawable.green_square),YELLOW(R.drawable.yellow_square),PINK(R.drawable.pink_square),
    ORANGE(R.drawable.orange_square),PURPLE(R.drawable.purple_square),DULL_GREEN(R.drawable.dull_green_square);

    private int drawabletTile;

    /**
     * Constructor.
     * @param tile
     */
    MyColor(int tile){
        drawabletTile = tile;
    }

    /**
     *  Get the drawable corresponding to the color.
     * @return
     */
    int getDrawable(){
        return drawabletTile;
    }
}
