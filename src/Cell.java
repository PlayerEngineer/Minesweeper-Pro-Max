import java.awt.*;

public class Cell {

    public static final int SIZE = 25;

    private boolean isMine;
    private int row, col, numMines;
    private boolean isRevealed;
    public final int x, y;
    private boolean flagged;
    private GameBoard board;

    public Cell(int row, int col, boolean isMine, GameBoard board){
        // TODO save the parameters to instance fields.  isRevealed should be false.
        this.row = row;
        this.col = col;
        this.isMine = isMine;
        this.board = board;

        x = col*SIZE;
        y = row*SIZE;
        isRevealed = false;

    }

    public void draw(Graphics2D g2){
        // TODO draw a square representing this cell
        //      draw the # of mines if revealed.
        GradientPaint GreenAndGreen = new GradientPaint(0, 0, new Color(5, 200, 0), 500, 500, new Color(2, 53, 0));
        GradientPaint brownAndBrown = new GradientPaint(0, 0, new Color(79, 58, 43), 500, 500, new Color(48, 37, 26));

        if(!isRevealed && !flagged){
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, SIZE, SIZE);
            g2.setPaint(GreenAndGreen);
            g2.fillRect(x, y, SIZE, SIZE);



        } else if (isRevealed && !flagged) {
            if(isMine) {
                g2.setColor(Color.red);
                g2.fillRect(x, y, SIZE, SIZE);
                g2.drawImage(Resources.smallMine, x, y, null);
            }
            else {

                g2.setPaint(brownAndBrown);
                //g2.setColor(new Color(79, 58, 43));
                g2.fillRect(x, y, SIZE, SIZE);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("", Font.BOLD, 25));

                if(numMines!=0)
                    g2.drawString("" + numMines, x + 4, y + 21);
            }
        } else if (!isRevealed && flagged) {

            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, SIZE, SIZE);
            g2.setPaint(GreenAndGreen);
            g2.fillRect(x, y, SIZE, SIZE);

            g2.setColor(Color.red);
            g2.fillRect(x+5, y+5, 18, 10);

            g2.setColor(new Color(150, 75, 0));
            g2.fillRect(x+5, y+5, 5, 20);


            //g2.fillRect(x, y, SIZE, SIZE);
        }

    }

    public void unFlag(){
        if(isRevealed)
            return;
        else flagged = false;
    }

    public void flag(){
        if(isRevealed)
            return;
        else
            flagged = true;

    }

    public void reveal(){
        isRevealed = true;

    }

    public void setNumMines(Cell[][] board){

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r == row && c == col || !isInBounds(r, c)) {
                    continue;
                }
                if (board[r][c].isMine) {
                    numMines++;

                }
            }
        }
    }

    public boolean isInBounds(int r, int c){
        return r > -1 && c > -1 && r < board.numRows && c < board.numCols;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean isMine() {
        return isMine;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isRevealed() {
        return isRevealed;

    }


    public int getNumMines() {
        return numMines;
    }

    public boolean isFlagged() {
        return flagged;
    }

}
