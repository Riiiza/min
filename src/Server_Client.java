import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class Server_Client extends javax.swing.JFrame 
{
    public static String pole;
    public static int flag=1;
    Random random = new Random();
    public static String b="";
    public static String test[][]=new String[9][9];
    public static String test1[][]=new String[9][9];
    public static byte[] buffer = new byte[65536];
    public static String Ip;
    public static String Port;
    public static DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
    public static ArrayList<Integer> al = new ArrayList<Integer>();
   
    public Server_Client() 
    {
  
        initComponents();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//Закрытие при нажатие на крестик
        setLocationRelativeTo(null);//Появление в центре экрана
        setResizable(false);//Запрет на изменения размера формы
        setTitle("Saper"); //Название формы
       
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(370, 230));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(76, 67, 163));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(76, 67, 163));
        jButton2.setText("Создать");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 110, 50));

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Port");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(76, 67, 163));
        jButton1.setText("Подключиться");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 140, 140, 50));

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Port");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, -1, -1));

        jTextField2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jTextField2.setText("7000");
        jPanel1.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 110, 109, -1));

        jTextField3.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jTextField3.setText("7000");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 110, -1));

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Подключиться");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, -1, -1));

        jTextField1.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jTextField1.setText("127.0.0.1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 60, 109, -1));

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("IP");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, -1, -1));

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Создать сервер");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 370, 230));

        pack();
    }// </editor-fold>                        

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        //Поток сервера
        Potoc2 = new AffableThread2();
        Potoc2.start();
       
    }                                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        //Поток клиента
        Potoc1 = new AffableThread1();
        Potoc1.start();
    
    }                                        

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    static AffableThread1 Potoc1;
    static AffableThread2 Potoc2;
        
    class AffableThread1 extends Thread//Нить работы клиента
    {
        @Override
        public void run()
        {
            DatagramSocket sock = null;
            //Считывание данных из TextField
            Ip = jTextField1.getText();
            Port = jTextField2.getText();
            Integer Port_int = Integer.valueOf(Port);
            try
            {
                sock = new DatagramSocket();
                random();//Генерация минного поля
                String s = b;
                byte[] b = s.getBytes();  
                //Отправка поля серверу
                DatagramPacket  dp = new DatagramPacket(b , b.length , InetAddress.getByName(Ip) , Port_int);
                sock.send(dp);
                sock.close();//Закрытие сокета
                exit_client();//Открытие самого поля для игры и закрытие меню
            }catch(IOException e)
            {
                 JOptionPane.showMessageDialog(null,"IOException " + e); 
                 System.exit(0);
            } 
        }
    }   
    class AffableThread2 extends Thread//Нить работы сервера
    {
        @Override
        public void run()
        {
            try
            {
            //Блокировка кнопок для ожидания поключения клиента
            jButton1.setEnabled(false);
            jButton2.setEnabled(false);
            JOptionPane.showMessageDialog(null, "Сервер создан, ожидайте подключения противника \n Игра начнется автоматически");
            Port = jTextField3.getText();
            Integer Port_int = Integer.valueOf(Port);    
                try (DatagramSocket sock = new DatagramSocket(Port_int)) 
                {   
                    //Получение данных о расположении мин от клиента
                    sock.receive(incoming);
                    byte[] data = incoming.getData();
                    String s = new String(data, 0, incoming.getLength());
                    pole = s;
                    int k=0;
                    //Форматирование полученных данных для последущего использования
                    for(int i = 0;i<10;i++)
                    {
                        char x = pole.charAt(k);
                        char y = pole.charAt(++k);
                        int x1 = Character.getNumericValue(x);
                        int y1 = Character.getNumericValue(y);  
                        test1[y1][x1]="b";
                        k=k+2;
                    }
                    sock.close();
                }
                
                    exit_server();   
            }
            catch(IOException e)
            {
                 JOptionPane.showMessageDialog(null,"IOException " + e); 
                 System.exit(0);
            } 
        }
    }  
    
    public void random()//Генерация поля с минами
    {
        b = "";
        int countMines=0;
        int x=0,y=0;
        while (countMines < 10) 
        {
            do 
            {
                x = random.nextInt(9);
                y = random.nextInt(9);
            } while (test[y][x] == "b");
            al.add(x);
            al.add(y);
            test[y][x]="b";
            countMines++;
            b=b+x;
            b=b+y;
            b=b+" ";  
        } 
        for(int i=0;i<al.size();i++)
        System.out.println(al.get(i));
      }
      
    void exit_client()//Закрытие клиента
    {
        new GameMines_Client().setVisible(true);
        this.dispose();
    } 
    void exit_server()//Закрытие сервера
    {
        new GameMines_server().setVisible(true);
        this.dispose();
    }

    public static void main(String args[]) 
    {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {      
        }     
        });
    }
    
    

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration                   
}