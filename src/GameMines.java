import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.Timer;

class GameMines extends JFrame {
    
    Timer timer = new Timer();
    volatile int time;
    final String TITLE_OF_PROGRAM = "Mines";//Имя формы 
    final String SIGN_OF_FLAG = "f";//Знак флага
    final int BLOCK_SIZE = 30; //размер блока в пиксилях
    final int FIELD_SIZE = 9; //Размер поля в блоках
    final int FIELD_DX = 6; //ширина рамок
    final int FIELD_DY = 28 + 17;//+место для таймера
    final int START_LOCATION = 200;//координаты появления окна
    final int MOUSE_BUTTON_LEFT = 1; //Кнопки мыши
    final int MOUSE_BUTTON_RIGHT = 3;
    final int NUMBER_OF_MINES = 10;//Количество мин
    final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0};//цвета для цифр
    Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];//Обьект класса Cell, поле игры
    Random random = new Random();
    int countOpenedCells;//Количество открытых ячеек
    boolean youWon, bangMine;
    int bangX, bangY; //координаты взрыва

    public static void main(String[] args) 
    {
        new GameMines_Client();
        
    }
    GameMines() {
      
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle(TITLE_OF_PROGRAM);
        setBounds(START_LOCATION, START_LOCATION, FIELD_SIZE * BLOCK_SIZE + FIELD_DX, FIELD_SIZE * BLOCK_SIZE + FIELD_DY);//Установка окна
        setResizable(false);
        final TimerLabel timeLabel = new TimerLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        final Canvas canvas = new Canvas();//определение панели
        canvas.setBackground(Color.white);//цвет панели
        canvas.addMouseListener(new MouseAdapter() {//отслеживание мыши
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);//вызов у родительского класса
                int x = e.getX()/BLOCK_SIZE;//координаты клика мышибузнаем ячейку
                int y = e.getY()/BLOCK_SIZE;
                if (!bangMine && !youWon) {
                    if (e.getButton() == MOUSE_BUTTON_LEFT) //нажата ли левая кнопка мыши
                        if (field[y][x].isNotOpen()) {//ячейка не открыта
                            openCells(x, y);//открываю ячейку
                            youWon = countOpenedCells == FIELD_SIZE*FIELD_SIZE - NUMBER_OF_MINES; //если открыта последняя ячейка
                            if (bangMine) {//взрыв мины
                                bangX = x;
                                bangY = y;
                            }
                    }
                    if (e.getButton() == MOUSE_BUTTON_RIGHT) field[y][x].inverseFlag(); //нажата правой кнопки мыши
                    if (bangMine)//Взорвана мина, игрок проиграл
                    {
                        canvas.repaint();//Обновление экрана
                        JOptionPane.showMessageDialog(null, "Игра окончена, вы проиграли\nВаше время " + String.format("%02d:%02d", time / 60, time % 60) ); 
                        close1();timeLabel.stopTimer();//закрытие формы и перенапровление в меню
                    } 
                    if (youWon)//игрок победил
                    {   
                        canvas.repaint();
                        JOptionPane.showMessageDialog(null, "Игра окончена, вы Выйграли\nВаше время " + String.format("%02d:%02d", time / 60, time % 60) ); 
                        close1();timeLabel.stopTimer();
                    } 
                    canvas.repaint();
                }
            }

           
        });
        add(BorderLayout.CENTER, canvas);//добавление поля
        add(BorderLayout.SOUTH, timeLabel);//добавление таймера
        setVisible(true);
        initField();//инициализация поля
    }
    void close1()
    {
        new Menu().setVisible(true);
        this.dispose();
    }
    void openCells(int x, int y) //открытие рядом стоящих пустых ячеек
    { 
        if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) return; //проверка координат поля
        if (!field[y][x].isNotOpen()) return; //если ячейка уже открыта
        field[y][x].open();//открытие 
        if (field[y][x].getCountBomb() > 0 || bangMine) return;//если стоит цифра либо бомба 
        for (int dx = -1; dx < 2; dx++)//открытие этого метода у соседних ячеек
            for (int dy = -1; dy < 2; dy++) openCells(x + dx, y + dy);
    }

    void initField() { //Инициализвация поля
        int x, y, countMines = 0;
        //создаем каждую клеточку, путем добавления обьектов Cell
        for (x = 0; x < FIELD_SIZE; x++)
        for (y = 0; y < FIELD_SIZE; y++)
        field[y][x] = new Cell();
        //генерация мин на поле 
        while(countMines < NUMBER_OF_MINES) 
        {
            do 
            {
                x = random.nextInt(FIELD_SIZE);
                y = random.nextInt(FIELD_SIZE);
            } while (field[y][x].isMined());
            field[y][x].mine();
            countMines++;
        } 
        //Считает количество бомб вокруг каждой ячейки
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
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                nX = x;
                                nY = y;
                            }
                            count += (field[nY][nX].isMined()) ? 1 : 0;
                        }
                    field[y][x].setCountBomb(count);//устанавливаем количество бомб вокруг
                }
    
      
        
    }
    public class Cell 
    { 
        private int countBombNear;
        private boolean isOpen, isMine, isFlag;

        void open()//открытие ячейки
        {
            isOpen = true;
            bangMine = isMine;
            if (!isMine) countOpenedCells++;
        }
        void mine() { isMine = true; }//минирование

        void setCountBomb(int count) { countBombNear = count; }//установка количества бомб

        int getCountBomb() { return countBombNear; }

        boolean isNotOpen() { return !isOpen; }//проверка открытия

        boolean isMined() { return isMine; }//проверка заминирования

        void inverseFlag() { isFlag = !isFlag; }//инвертирование флага

        void paintBomb(Graphics g, int x, int y, Color color)//нарисовать бомбу по пикселям
        {
            g.setColor(color);
            g.fillRect(x*BLOCK_SIZE + 7, y*BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x*BLOCK_SIZE + 9, y*BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x*BLOCK_SIZE + 11, y*BLOCK_SIZE + 10, 4, 4);
        }

        void paintString(Graphics g, String str, int x, int y, Color color) //нарисовать строку
        {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x*BLOCK_SIZE + 8, y*BLOCK_SIZE + 26);
        }

        void paint(Graphics g, int x, int y)
        {
            //расчерчивание поля
            g.setColor(Color.lightGray);
            g.drawRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            if (!isOpen) 
            {
                if ((bangMine || youWon) && isMine) paintBomb(g, x, y, Color.black);//рисуется бомба
                else 
                {
                    //рисуем пямоугольник
                    g.setColor(Color.lightGray);
                    g.fill3DRect(x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) paintString(g, SIGN_OF_FLAG, x, y, Color.red);//Рисуется флаг
                }
            } else
                if (isMine) paintBomb(g, x, y, bangMine? Color.red : Color.black);//рисуется бомба
                else
                    if (countBombNear > 0)
                        paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));//Рисуем цифру
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
            public void run()
            {
                time++;
                SwingUtilities.invokeLater(refresher);
            }
        };

        void stopTimer() { timer.cancel(); }
    }
    class Canvas extends JPanel//отрисовка
    {
        @Override
        public void paint(Graphics g) 
        {
            super.paint(g);//вызов отрисовки
            for (int x = 0; x < FIELD_SIZE; x++)
                for (int y = 0; y < FIELD_SIZE; y++) field[y][x].paint(g, x, y);//сама отрисовка
        }
    }
   
}