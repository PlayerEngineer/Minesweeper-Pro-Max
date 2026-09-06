import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MinesweeperPanel extends JPanel {

    public static final int WIDTH=900, HEIGHT=800;

    public GameBoard board;
    public boolean locked;
    SoundPlayer sp = new SoundPlayer();
    private Graphics2D g2;
    private Timer timer;
    private int time;



    public MinesweeperPanel() {
        super();
        setSize(WIDTH, HEIGHT);
        board = new GameBoard(25, 25, 70);
        time = 0;
        timer = new Timer(1000, e->update());
        timer.start();

        sp.addSound("win", "./audios/win.wav");

        setupInput();
    }

    public void update(){
        time++;
        if(board.win)
            timer.stop();
        repaint();
    }

    public void setupInput(){

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE && !locked){
                    Point mousePos = getMousePosition();
                    int row = mousePos.y / 25;
                    int col = mousePos.x / 25;
                    board.flag(row, col);
                    repaint();
                }

                if(e.getKeyCode() == KeyEvent.VK_P){
                    Point mousePos = getMousePosition();
                    int row = mousePos.y / 25;
                    int col = mousePos.x / 25;
                    board.chord(row, col);
                    repaint();
                }

                if(e.getKeyCode() == KeyEvent.VK_R){
                    board.restart(25, 25, 70);
                    timer.start();
                    time= 0;
                    locked = false;
                    repaint();
                }

                if(e.getKeyCode() == KeyEvent.VK_MINUS){
                    board.finish();
                    repaint();
                }

            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(e.getSource() instanceof JButton restart){
                    if(restart.getText().equals("Restart")){
                        board.restart(25, 25, 70);
                        locked = false;
                        time = 0;
                        timer.start();
                        repaint();
                        grabFocus();

                    }
                }else {

                    int row = e.getY() / 25;
                    int col = e.getX() / 25;
                    if (e.getButton() == 1 && board.isInBounds(row, col) && !board.isRevealed(row, col) && !locked)
                        board.reveal(row, col, true);

                    else if (e.getButton() == 1 && board.isInBounds(row, col) && board.isRevealed(row, col) && !locked)
                        board.chord(row, col);
                    repaint();

                    if (e.getButton() == 3 && board.isInBounds(row, col) && !locked) {
                        board.flag(row, col);
                        repaint();
                    }
                    repaint();
                }
            }
        });
    }

    public void drawGameOver(Graphics2D g2){
        this.g2 = g2;
        timer.stop();

        g2.setColor(Color.RED);
        //g2.setFont(new Font("", Font.BOLD, 70));
        //g2.drawString("Game Over", getWidth()/3, getHeight()/3);

        //g2.fillRect(0, 0, 1000, 1000);
        setBackground(Color.RED);
    }

    public void drawUI(Graphics2D g2){
        if(!board.win)
            setBackground(Color.GRAY);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("", Font.BOLD, 15));

        g2.setColor(Color.red);

        GradientPaint redAndRed = new GradientPaint(625+5, 100+5, new Color(255, 0, 0), 625+5+54, 100+5+30, new Color(96, 0, 0));
        g2.setPaint(redAndRed);
        g2.fillRect((625 + 5) , (100 + 5), 54, 30);

        GradientPaint brownAndBrown = new GradientPaint(625+5, 100+5, new Color(150, 75, 0), 625+5+15, 100+5+60, new Color(67, 33, 0));
        g2.setPaint(brownAndBrown);
        g2.fillRect((625 + 5), (100 + 5), 15, 60);

        g2.setFont(new Font("", Font.BOLD, 40));
        g2.drawString(""+board.flagCount(), 625+75, 100+50);

        GradientPaint greyAndGray = new GradientPaint(625+150, 100, new Color(87, 87, 87), 625+250, 100, new Color(48, 48, 48));
        g2.setPaint(greyAndGray);
        g2.fillOval(625+130, 100, 150,100);
        //g2.fillOval(625+150, 100, 100,100);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("", Font.BOLD, 40));

        g2.drawString(""+time, 625+185, 165);

        GradientPaint greenAndGreen = new GradientPaint(625+10, 200, new Color(4, 129, 0), 625+110, 300, new Color(2, 53, 0));
        g2.setPaint(greenAndGreen);
        g2.fillRect(625 + 10, 200, 100, 100);

        g2.setColor(Color.WHITE);
        g2.drawString(""+board.globalCount, 625+20, 265);

    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                g2.drawImage(Resources.background, 100*r, 100*c, null);
            }


        }


        board.draw(g2);
        drawUI(g2);

        board.winCheck();

        if(board.win && !board.played){
            sp.playSound("win");
            board.played = true;
            locked = true;
            setBackground(Color.GREEN);
        }

        board.gameOverCheck();
        if(board.gameOver) {

            for (int r = 0; r < board.numRows; r++) {
                for (int c = 0; c < board.numCols; c++) {
                    board.reveal(r, c, false);
                }
            }

            drawGameOver(g2);
            locked = true;
            repaint();
        }
    }


    public static void main(String[] args) {
        JFrame window = new JFrame("🗿🥶🥶Minesweeper Tough Edition🥶🥶🗿");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        window.setBounds(0, 0, WIDTH, HEIGHT + 28); // title bar is 28 pixels!
        JPanel panel = new MinesweeperPanel();
        panel.setLayout(null);

        JButton restart = new JButton("Restart");
        restart.setBounds(625,0,275, 100);
        restart.setBackground(Color.RED);
        restart.setVisible(true);
        panel.add(restart);


        restart.addMouseListener(panel.getMouseListeners()[0]);



        panel.setFocusable(true);
        panel.grabFocus();
        window.add(panel);
        window.setVisible(true);
        window.setResizable(false);
    }
}