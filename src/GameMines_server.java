import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.swing.*;
import java.util.*;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

class GameMines_server extends JFrame {
    String Flag="0";
    DatagramSocket sock = null;
    Timer timer = new Timer();
    public static volatile int time;
    final String TITLE_OF_PROGRAM = "Mines";
    final String SIGN_OF_FLAG = "f";
    final int BLOCK_SIZE = 30;
    final int FIELD_SIZE = 9; 
    final int FIELD_DX = 6; 
    final int FIELD_DY = 28 + 17;
    final int START_LOCATION = 200;
    final int MOUSE_BUTTON_LEFT = 1;
    final int MOUSE_BUTTON_RIGHT = 3;
    final int NUMBER_OF_MINES = 10;
    final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0};
    Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];
    Random random = new Random();
    int countOpenedCells;
    boolean youWon, bangMine;
    int bangX, bangY; 

    public static void main(String[] args) 
    {
        new GameMines_Client();   
    }
    GameMines_server() 
    {
        Potoc1 = new AffableThread1();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle(TITLE_OF_PROGRAM);
        setBounds(START_LOCATION, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_DX, FIELD_SIZE * BLOCK_SIZE + FIELD_DY);
        setResizable(false);
        final TimerLabel timeLabel = new TimerLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        final Canvas canvas = new Canvas();
        canvas.setBackground(Color.white);
        canvas.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseReleased(MouseEvent e) 
            {
                super.mouseReleased(e);
                int x = e.getX()/BLOCK_SIZE;
                int y = e.getY()/BLOCK_SIZE;
                if (!bangMine && !youWon) 
                {
                    if (e.getButton() == MOUSE_BUTTON_LEFT) 
                        if (field[y][x].isNotOpen()) 
                        {
                            openCells(x, y);
                            youWon = countOpenedCells == FIELD_SIZE*FIELD_SIZE - NUMBER_OF_MINES; 
                            if (bangMine) 
                            {
                                bangX = x;
                                bangY = y;
                            }
                        }
                    if (e.getButton() == MOUSE_BUTTON_RIGHT) field[y][x].inverseFlag(); 
                    if (bangMine)
                    { 
                       
                        canvas.repaint();
                        Flag="1";
                        try 
                        {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) 
                        {
                            Logger.getLogger(GameMines_Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                       Potoc1.stop();
                        JOptionPane.showMessageDialog(null, "Игра окончена, вы проиграли\nВаше время " + String.format("%02d:%02d", time / 60, time % 60) ); 
                        close1();timeLabel.stopTimer();
                    } 
                    if (youWon)
                    {   
                        canvas.repaint();
                         Flag="2";
                        try 
                        {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) 
                        {
                            Logger.getLogger(GameMines_Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Potoc1.stop();
                        JOptionPane.showMessageDialog(null, "Игра окончена, вы Выйграли\nВаше время " + String.format("%02d:%02d", time / 60, time % 60) ); 
                        close1();timeLabel.stopTimer();
                        
            
                    } 
                    canvas.repaint();
                }
            }

           
        });
        add(BorderLayout.CENTER, canvas);
        add(BorderLayout.SOUTH, timeLabel);
        setVisible(true);
        initField();
    }
    void close1()
    {
        sock.close();
        obnul();
        new Menu().setVisible(true);
        this.dispose();
    }
    void openCells(int x, int y) 
    { 
        if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return; 
        if (!field[y][x].isNotOpen()) return; 
        field[y][x].open();
        if (field[y][x].getCountBomb() > 0 || bangMine) return; 
        for (int dx = -1; dx < 2; dx++)
            for (int dy = -1; dy < 2; dy++) openCells(x + dx, y + dy);
    }

    void initField() 
    { 
        Potoc1.start();
        int x, y;
 
        for (x = 0; x < FIELD_SIZE; x++)
        for (y = 0; y < FIELD_SIZE; y++)
        field[y][x] = new Cell();
     
         for (x = 0; x < FIELD_SIZE; x++){
            for (y = 0; y < FIELD_SIZE; y++){
                if(Server_Client.test1[x][y]=="b")field[y][x].mine();//заполняем поле бомбами которые сгенерировал клиент и отправили нам
            }}

        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                if (!field[y][x].isMined()) {
                    int count = 0;
                    for (int dx = -1; dx < 2; dx++)
                        for (int dy = -1; dy < 2; dy++) {                    
                            int nX = x + dx;
                            int nY = y + dy;
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                nX = x;
                                nY = y;
                            }
                            count += (field[nY][nX].isMined()) ? 1 : 0;
                        }
                    field[y][x].setCountBomb(count);
                }
    
    }
    public class Cell 
    {
        private int countBombNear;
        private boolean isOpen, isMine, isFlag;

        void open() 
        {
            isOpen = true;
            bangMine = isMine;
            if (!isMine) countOpenedCells++;
        }
        void mine() { isMine = true; }

        void setCountBomb(int count) { countBombNear = count; }

        int getCountBomb() { return countBombNear; }

        boolean isNotOpen() { return !isOpen; }

        boolean isMined() { return isMine; }

        void inverseFlag() { isFlag = !isFlag; }

        void paintBomb(Graphics g, int x, int y, Color color) 
        {
            g.setColor(color);
            g.fillRect(x*BLOCK_SIZE + 7, y*BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x*BLOCK_SIZE + 9, y*BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 10, 4, 4);
        }

        void paintString(Graphics g, String str, int x, int y, Color color) 
        {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x*BLOCK_SIZE + 8, y*BLOCK_SIZE + 26);
        }

        void paint(Graphics g, int x, int y)
        {
            g.setColor(Color.lightGray);
            g.drawRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            if (!isOpen) 
            {
                if ((bangMine || youWon) && isMine) paintBomb(g, x, y, Color.black);
                else 
                {
                    g.setColor(Color.lightGray);
                    g.fill3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) paintString(g, SIGN_OF_FLAG, x, y, Color.red);
                }
            } else
                if (isMine) paintBomb(g, x, y, bangMine? Color.red : Color.black);
                else
                    if (countBombNear > 0)
                        paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));
        }

        
    }
    class TimerLabel extends JLabel 
    { 
        

        TimerLabel() { timer.scheduleAtFixedRate(timerTask, 0, 1000); } 

        TimerTask timerTask = new TimerTask() 
        {
           
            Runnable refresher = new Runnable() 
            {
                public void run() 
                {
                    TimerLabel.this.setText(String.format("%02d:%02d", time / 60, time % 60));
                }
            };
            public void run() {
                time++;
                SwingUtilities.invokeLater(refresher);
            }
        };

        void stopTimer() { timer.cancel(); }
    }

    class Canvas extends JPanel 
    { 
        @Override
        public void paint(Graphics g) 
        {
            super.paint(g);
            for (int x = 0; x < FIELD_SIZE; x++)
                for (int y = 0; y < FIELD_SIZE; y++) field[y][x].paint(g, x, y);
        }
    }
static AffableThread1 Potoc1;

class AffableThread1 extends Thread//обмен информацци между игроками
    {
        @Override
        public void run()
        {
         try
        {

            Integer Port_int = Integer.valueOf(Server_Client.Port); 
             sock = new DatagramSocket(Port_int);
             

            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
             

             
            while(true)
            {

                sock.receive(incoming);
                byte[] data = incoming.getData();
                String s = new String(data, 0, incoming.getLength());
                

                Integer Win = Integer.valueOf(s); 
                if(Win==1)
                {
                    sock.close();

                    JOptionPane.showMessageDialog(null, "Противник проиграл, вы победили" ); 
                    close1();
                }
                else if(Win==2)
                {
                    sock.close();

                    JOptionPane.showMessageDialog(null, "Противник выйграл раньше, вы проиграли" ); 
                    close1();
                }
                
                DatagramPacket dp = new DatagramPacket(Flag.getBytes() , Flag.getBytes().length , incoming.getAddress() , incoming.getPort());
                sock.send(dp); 
                
            }
        }
        catch(IOException e)
        {

        }
        }
     
    }
    void obnul()
    {
         for (int x = 0; x < FIELD_SIZE; x++){
            for (int y = 0; y < FIELD_SIZE; y++){
                if(Server_Client.test1[x][y]=="b")Server_Client.test1[x][y]="a";
            }}
    }
  
}