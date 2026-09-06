import javax.sound.sampled.Clip;
import java.awt.*;

public class GameBoard {

    private Cell[][] cells;
    public int numRows;
    public int numCols;
    public int numMineCount;
    public boolean gameOver;
    public boolean win;
    SoundPlayer sp = new SoundPlayer();
    public boolean played = false;
    public int freeSpaces;
    public int globalCount;

    public GameBoard(int rows, int cols, int mineCount){
        cells = new Cell[rows][cols];
        this.numRows = rows;
        this.numCols = cols;
        this.numMineCount = mineCount;
        freeSpaces = cells.length*cells[0].length - numMineCount;

        sp.addSound("dbButtonClickFlag", "./audios/dbButtonClickFlag.wav");
        sp.addSound("firstPaint", "./audios/firstPaint.wav");
        sp.addSound("mineBombEnd", "./audios/mineBombEnd.wav");
        sp.addSound("mineBombRun", "./audios/mineBombRun.wav");
        sp.addSound("mineBombStart", "./audios/mineBombStart.wav");
        sp.addSound("zeroDevelop", "./audios/zeroDevelop.wav");
        sp.addSound("shovel", "./audios/shovel.wav");

        sp.playSound("firstPaint");

        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                cells[r][c] = new Cell(r, c, false, this);
            }

        }


        //sprinkle some mines here and there
        while (mineCount > 0) {
            for (int r = 0; r < cells.length; r++) {
                for (int c = 0; c < cells[r].length; c++) {
                    if (Math.random() > 0.9 && !cells[r][c].isMine() && mineCount > 0) {
                        cells[r][c].setMine(true);
                        mineCount--;
                    }
                }
            }
        }


        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                cells[r][c].setNumMines(cells);
            }
        }

    }

    public void winCheck(){
        int count = 0;
        freeSpaces = cells.length*cells[0].length - numMineCount;
        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[r].length; c++) {
                if(!cells[r][c].isMine()&&cells[r][c].isRevealed()) {
                    count++;
                }
                if(count==freeSpaces && !gameOver && !win) {
                    win = true;
                }
            }
        }
        globalCount = count;
    }

    public void gameOverCheck(){
        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                if (cells[r][c].isMine() && cells[r][c].isRevealed() && !gameOver) {
                    new Thread(() -> {
                        sp.playSound("mineBombStart");

                        try {
                            Thread.sleep(129);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        sp.playSound("mineBombRun");
                        try {
                            Thread.sleep(3950);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        sp.playSound("mineBombEnd");

                    }).start();
                    gameOver = true;

                    break;
                }
            }
        }

    }

    public void finish(){

        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                cells[r][c].unFlag();

            }
        }



        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                if(cells[r][c].isMine())
                    flag(r,c);
                else
                    reveal(r, c, false);

            }
        }

    }

    public int countFlagsAround(int r, int c){
        int count = 0;

        for (int i = r - 1; i <= r + 1; i++) {
            for (int j = c - 1; j <= c + 1; j++) {
                if (i == r && j == c || !isInBounds(i, j))
                    continue;


                if (cells[i][j].isFlagged())
                    count++;
            }
        }

        return count;
    }


    public void chord(int r, int c){

        int counter = 0;
        if(countFlagsAround(r, c) != cells[r][c].getNumMines()) {
            return;
        }

        if(!cells[r][c].isRevealed())
            return;

        else if (cells[r][c].isRevealed() && countFlagsAround(r, c) == cells[r][c].getNumMines()) {
            for (int i = r - 1; i <= r + 1; i++) {
                for (int j = c - 1; j <= c + 1; j++) {
                    if (i == r && j == c)
                        continue;

                    if (!isInBounds(i, j))
                        continue;

                    if (!cells[i][j].isRevealed()) {
                        reveal(i, j, false);
                        counter++;
                    }
                }
            }
        }

        if(counter>=3)
            sp.playSound("zeroDevelop");
    }

    public boolean isRevealed(int rows, int cols){
        return cells[rows][cols].isRevealed();
    }

    public void restart(int rows, int cols, int mineCount){
        played = false;
        win = false;
        sp.playSound("firstPaint");

        cells = new Cell[rows][cols];
        gameOver = false;

        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                cells[r][c] = new Cell(r, c, false, this);
            }
        }


        //sprinkle some mines here and there
        while (mineCount > 0) {
            for (int r = 0; r < cells.length; r++) {
                for (int c = 0; c < cells[r].length; c++) {
                    if (Math.random() > 0.9 && !cells[r][c].isMine() && mineCount > 0) {
                        cells[r][c].setMine(true);
                        mineCount--;
                    }
                }
            }
        }


        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                cells[r][c].setNumMines(cells);
            }
        }



    }

    public void draw(Graphics2D g2){
        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                cells[r][c].draw(g2);
            }

        }

    }

    public boolean isInBounds(int r, int c){
        return r > -1 && c > -1 && r < cells.length && c < cells[0].length;
    }


    public void flag(int r, int c){

        if(isInBounds(r, c) && !cells[r][c].isFlagged()) {
            cells[r][c].flag();
            sp.playSound("dbButtonClickFlag");
        }
        else if (isInBounds(r, c) && cells[r][c].isFlagged()) {
            cells[r][c].unFlag();
            sp.playSound("dbButtonClickFlag");
        }
    }

    public int flagCount(){
        int count = 0;

        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                if(cells[r][c].isFlagged())
                    count++;
            }
        }
        return count;
    }

    public void reveal(int r, int c, boolean clicked) {

        if (cells[r][c].isRevealed()) {
            return;

        }else if (!clicked && cells[r][c].isFlagged() && !cells[r][c].isMine()) {
            cells[r][c].unFlag();
            cells[r][c].reveal();
        }

        else if (cells[r][c].isFlagged() && cells[r][c].isMine()) {
            return;
        }

        if(cells[r][c].isFlagged())
            return;

        cells[r][c].reveal();
        if(clicked)
            sp.playSound("shovel");

        if (cells[r][c].getNumMines() > 0) {
            return;
        }

        for (int i = r - 1; i <= r + 1; i++) {
            for (int j = c - 1; j <= c + 1; j++) {
                if (i == r && j == c)
                    continue;


                if (!isInBounds(i, j))
                    continue;


                if (!cells[i][j].isRevealed())
                    reveal(i, j, false);
            }
        }
    }

}
