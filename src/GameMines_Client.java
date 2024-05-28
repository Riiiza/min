import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.*;
import java.util.*;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

class GameMines_Client extends JFrame {
    
    Timer timer = new Timer();
    String Flag="0";
    volatile int time;
    final String TITLE_OF_PROGRAM = "Mines";
    final String SIGN_OF_FLAG = "f";
    final int BLOCK_SIZE = 30; 
    final int FIELD_SIZE = 9; 
    final int FIELD_DX = 6; 
    final int FIELD_DY = 28 + 17;
    final int START_LOCATION = 400;
    final int MOUSE_BUTTON_LEFT = 1; 
    final int MOUSE_BUTTON_RIGHT = 3;
    final int NUMBER_OF_MINES = 10;
    final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0};
    Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];
    DatagramSocket sock;
    Random random = new Random();
    int countOpenedCells;
    boolean youWon, bangMine; 
    int bangX, bangY;

    public static void main(String[] args) 
    {
        new GameMines_Client();   
    }
    GameMines_Client() 
    {
        Potoc2 = new AffableThread2();
        
        Potoc2.start();
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
                        }catch (InterruptedException ex) 
                        {
                            Logger.getLogger(GameMines_Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Potoc2.stop();
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
                        Potoc2.stop();
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
        int x, y;
        for (x = 0; x < FIELD_SIZE; x++)
        for (y = 0; y < FIELD_SIZE; y++)
        field[y][x] = new Cell();
        for(int i=0;i<Server_Client.al.size();i=i+2)//заполняем поле бомбами которые сгенерировали и отправили на сервер
        {
           Integer p = Server_Client.al.get(i); 
           Integer g =Server_Client.al.get(i+1); 
           field[p][g].mine();
        }

        for (x = 0; x < FIELD_SIZE; x++)
            for (y = 0; y < FIELD_SIZE; y++)
                if (!field[y][x].isMined()) 
                {
                    int count = 0;
                    for (int dx = -1; dx < 2; dx++)
                        for (int dy = -1; dy < 2; dy++) 
                        {                    
                            int nX = x + dx;
                            int nY = y + dy;
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1)
                            {
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
                public void run() {
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
            {
                for (int y = 0; y < FIELD_SIZE; y++)
                {
                    field[y][x].paint(g, x, y);
                }
            }
                
        }
    }
   
    static AffableThread2 Potoc2;
    
    class AffableThread2 extends Thread//поток отвечающий за обмен информацией между игроками в режиме реального времени
    {
     @Override
        public void run()
        {
        sock = null;
        Integer Port_int = Integer.valueOf(Server_Client.Port); 
        try
        {
            sock = new DatagramSocket();
             
            while(true)
            {
                try 
                {
                    Thread.sleep(100);
                } catch (InterruptedException ex) 
                {
                    Logger.getLogger(GameMines_Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                String s = Flag;
                byte[] b = s.getBytes();
                

                DatagramPacket  dp = new DatagramPacket(b , b.length , InetAddress.getByName(Server_Client.Ip) , Port_int);
                sock.send(dp);
                 
 
                byte[] buffer = new byte[65536];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                

                sock.receive(reply);
                byte[] data = reply.getData();
                s = new String(data, 0, reply.getLength());
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
                    JOptionPane.showMessageDialog(null,  "Противник выйграл раньше, вы проиграли" ); 
                    close1();
                }   
            }
        }catch(IOException e)
        { 
            
        }
        }
   
   
    }
    void obnul()//обнуление поля после окончания игры
    {
        for (int x = 0; x < FIELD_SIZE; x++)
        {
            for (int y = 0; y < FIELD_SIZE; y++)
            {
                if(Server_Client.test[x][y]=="b")Server_Client.test[x][y]="a";
            }
        }
        Server_Client.al.clear();
    }
 
}