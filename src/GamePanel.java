import org.w3c.dom.css.RGBColor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener{


    static final String MAP_PATH="C:/Users/kacpe/Desktop/Warriormon/src/maps/map1.txt";
    static final int indicatorOfScale = 2;
    static final int SCREEN_WIDTH = 400*indicatorOfScale;
    static final int SCREEN_HEIGHT = 400*indicatorOfScale;
    static final int UNIT_SIZE = 25*indicatorOfScale;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
    static final int DELAY = 50;
    int x;
    int y;

    static ImageIcon characterImageIcon;


    static String mapName;
    static int mapSizeY = 16;
    static int mapSizeX = 16;
    static String[][] MAP_GRID = new String[mapSizeY][mapSizeX];

    boolean running = false;
    Timer timer;


    GamePanel(){
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame() {
        getCharacterImage();
        createMap();
        setStartingPosition(1*UNIT_SIZE,14*UNIT_SIZE);
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
        //printMAP_GRID();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {

        if(running) {
            //color of specific squares on map
            for(int i = 0; i < mapSizeY; i++)
                for(int j = 0; j < mapSizeX;j++){
                    if(Integer.parseInt(MAP_GRID[i][j])==0){
                        g.setColor(new Color(20, 13, 7));
                    }else if(Integer.parseInt(MAP_GRID[i][j])==1){
                        g.setColor(new Color(56, 175, 205));
                    }else if(Integer.parseInt(MAP_GRID[i][j])==2){
                        g.setColor(new Color(56, 128, 4));
                    }else if(Integer.parseInt(MAP_GRID[i][j])==3){
                        g.setColor(new Color(130, 73, 11));
                    }

                    g.fillRect(i*UNIT_SIZE,j*UNIT_SIZE, UNIT_SIZE,UNIT_SIZE); //zmienilem i z j
                }


            for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }


            //Character color
            g.setColor(Color.red);
            g.fillRect(x+UNIT_SIZE/4, y+UNIT_SIZE/4, UNIT_SIZE/2, UNIT_SIZE/2);


            g.setColor(Color.red);
            g.setFont( new Font("Times New Roman",Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(mapName, (SCREEN_WIDTH - metrics.stringWidth(mapName))/2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }

    }

    int wrongCollisionCounter=0;
    public boolean checkCollisions(int x, int y) {
        boolean isAllowedToMakeAMove = true;


        //check if head touches left border
        if(x < 0) {
            x=0;
            return false;
        }
        //check if head touches right border
        if(x >= SCREEN_WIDTH) {
            x=SCREEN_WIDTH-UNIT_SIZE;
            return false;
        }
        //check if head touches top border
        if(y < 0) {
            y=0;
            return false;
        }
        //check if head touches bottom border
        if(y >= SCREEN_HEIGHT) {
            y=SCREEN_HEIGHT-UNIT_SIZE;
            return false;
        }

        if(MAP_GRID[x/UNIT_SIZE][y/UNIT_SIZE].equals("0") || MAP_GRID[x/UNIT_SIZE][y/UNIT_SIZE].equals("1") || MAP_GRID[x/UNIT_SIZE][y/UNIT_SIZE].isEmpty()){
            isAllowedToMakeAMove=false;
            wrongCollisionCounter++;
            System.out.println(wrongCollisionCounter+ ". Move cannot be done, collision is not correct");
        }

        if(!running) {
            timer.stop();
        }
        return isAllowedToMakeAMove;
    }
    public void gameOver(Graphics g) {

        //Game Over text
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if(running) {
            checkCollisions(x,y);
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_A:
                    if(checkCollisions(x-UNIT_SIZE,y))
                        x = x - UNIT_SIZE;
                    break;
                case KeyEvent.VK_D:
                    if(checkCollisions(x+UNIT_SIZE,y))
                        x = x + UNIT_SIZE;
                    break;
                case KeyEvent.VK_W:
                    if(checkCollisions(x,y-UNIT_SIZE))
                        y = y - UNIT_SIZE;
                    break;
                case KeyEvent.VK_S:
                    if(checkCollisions(x,y+UNIT_SIZE))
                        y = y + UNIT_SIZE;
                    break;
            }
        }
    }




    public static void createMap(){
        ArrayList<String[]> arrayListOfMapGridLinesFromFile = new ArrayList<>();
        FileReader in = null;
        try {




            in = new FileReader(MAP_PATH);
            BufferedReader br = new BufferedReader(in);
            while (br.ready()) {
                String currentLine = br.readLine();

                if(!currentLine.startsWith("//")) {
                    if (currentLine.startsWith("mapName:"))
                        mapName = currentLine.split("mapName: ")[1];

                    else {
                        arrayListOfMapGridLinesFromFile.add(currentLine.split(""));
                    }
                }
            }






        } catch (IOException e) {
            System.out.println("There is no map file in directory: " + MAP_PATH + "\n Or BufferedReader could not read line");
        }

        for(int i = 0; i<mapSizeY;i++) {
            for(int j = 0; j<mapSizeX;j++){
                MAP_GRID[i][j]=arrayListOfMapGridLinesFromFile.get(j)[i];

            }
        }

    }

    public static void getCharacterImage(){
        try {
            BufferedImage img = ImageIO.read(new File("C:/Users/kacpe/Desktop/MovementNet/out/production/MovementNet/sprites/knight50x50reworked.png"));
            characterImageIcon = new ImageIcon(img);



        } catch (IOException e) {
            System.out.println("Cannot load Character image");
        }

    }


    public void setStartingPosition(int x, int y){
        this.x=x;
        this.y=y;
    }


    public static void printMAP_GRID() {
        for (int i = 0; i < mapSizeY; i++){
            System.out.print("\n");
            for (int j = 0; j < mapSizeX; j++)
                System.out.print(MAP_GRID[i][j]);
        }
    }
}