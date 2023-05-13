package alogrithm;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class Algorithm4Direction extends JFrame implements ActionListener {
    // (10, 10) -> map rat nho
    public static int[] EXTRA_SMALL = {10, 10};
    // (20, 20) -> map nho
    public static int[] SMALL = {20, 20};
    // (40, 40) -> map vua
    public static int[] MEDIUM = {40, 40};
    // (80, 80) -> map lon
    public static int[] LARGE = {80, 80};
    // (160, 160) -> map rat lon
    public static int[] EXTRA_LARGE = {160, 160};

    // mang y the hien di chuyen theo chieu doc, mang x the hien di chuyen theo chieu ngang
    // y-1, x+0 --> di len tren
    // y+1, x+0 --> di xuong duoi
    // y+0, x-1 --> di sang trai
    // y+0, x+1 --> di sang phai
    private static int[] moveY = {-1, 1, 0, 0};
    private static int[] moveX = {0, 0, -1, 1};

    private static int MAX_WIDTH = 800;
    private static int MAX_HEIGHT = 800;
    private int buttonSize; // kich co cua mot o trong map
    private boolean[][] map;  // ban do di chuyen
    private Point start, goal; // diem bat dau va ket thuc
    private int row, column; // gioi han chieu cao va chieu rong cua map
    private JPanel mainPanel; // panel dai dien cho ban do di chuyen (hien thi len tren man hinh)
    private JPanel menuPanel; // menu chuc nang

    // cac nut bam cua bang menu
    private JButton btnRun, btnReload, btnRenew, btnRandomMaze, btnStartPoint, btnGoalPoint, btnBlockPoint, btnExit;

    // bien the hien trang thai cua cac nut trong bang menu
    private boolean isStartPointOn, isGoalPointOn, isInitPointOn;
    private static Color defaultBtnColor;

    private static long delayTime;

    public Algorithm4Direction(int[] size){
        row = size[0];
        column = size[1];
        buttonSize = MAX_HEIGHT/row;
        map = new boolean[row][column];
        start = null;
        goal = null;
        setDelayTime();
        display();
    }

    private void setDelayTime() {
        switch (row) {
            case 160:
                delayTime = 1;
                break;
            case 80:
                delayTime = 5;
                break;
            case 40:
                delayTime = 10;
                break;
            case 20:
                delayTime = 15;
                break;
            default:
                delayTime = 20;
        }
    }

    // kiem tra nuoc di hop le (co di ra ngoai map khong?)
    private boolean isValid(Point point) {
        int x = point.x, y = point.y;
        return x >= 0 && x < row && y >= 0 && y < column;
    }

    // kiem tra o nay co bi chan khong
    private boolean isBlocked(Point point) {
        int x = point.x, y = point.y;
        return map[x][y];
    }

    // tinh gia tri heuristic su dung Manhattan Distance
    private int heuristic(Point point) {
        int x = point.x, y = point.y;
        int dx =  Math.abs(x-goal.x);
        int dy =  Math.abs(y-goal.y);
        return dx+dy;
    }

    private void aStarAlgorithm() {
        // Disable buttons while the algorithm is running
        btnRun.setEnabled(false);
        btnRenew.setEnabled(false);
        btnRandomMaze.setEnabled(false);
        btnBlockPoint.setEnabled(false);
        btnGoalPoint.setEnabled(false);
        btnReload.setEnabled(false);
        btnStartPoint.setEnabled(false);

        // Create a SwingWorker to perform the algorithm in the background
        SwingWorker<Void, Point> worker = new SwingWorker<Void, Point>() {
            private Color currentColor;
            @Override
            protected Void doInBackground() throws Exception {
                // Algorithm code here, instead of calling aStarAlgorithm directly
                // Use publish() to periodically update the GUI in the process
                currentColor = Color.CYAN;

                // tap dong
                HashSet<Node> closedList = new HashSet<>();
                // tap mo
                PriorityQueue<Node> openList = new PriorityQueue<>();
                // ma tran chua gia tri f(n)
                int[][] f = new int[row][column];

                Node first = new Node(start, null, 0, heuristic(start));
                openList.add(first);

                while(!openList.isEmpty()) {
                    Node node = openList.poll();

                    // neu day la dich
                    if(node.point.equals(goal)) {
                        JOptionPane.showMessageDialog(null, String.format("It's take %d steps to reach the goal", node.f));
                        currentColor = Color.YELLOW;
                        while(node != null) {
                            publish(node.point);
                            Thread.sleep(delayTime*2);
                            node = node.parent;
                        }
                        return null;
                    }

                    // neu da co trong tap dong
                    if(closedList.contains(node)) {
                        int previousF = f[node.point.x][node.point.y];
                        if(node.f < previousF) {
                            f[node.point.x][node.point.y] = node.f;
                            closedList.remove(node);
                            closedList.add(node);
                        }
                    }
                    else {
                        f[node.point.x][node.point.y] = node.f;
                        closedList.add(node);
                    }

                    for(int i = 0; i < 4; i++) {
                        int newX = node.point.x + moveX[i];
                        int newY = node.point.y + moveY[i];
                        Point newPoint = new Point(newX, newY);
                        if(isValid(newPoint) && !isBlocked(newPoint)) {
                            Node neighbour = new Node(newPoint, node, node.g + 1, node.g + 1 + heuristic(newPoint));
                            // neu da co trong tap mo
                            if(openList.contains(neighbour)) {
                                int previousF = f[neighbour.point.x][neighbour.point.y];
                                if(neighbour.f < previousF) {
                                    f[neighbour.point.x][neighbour.point.y] = neighbour.f;
                                    openList.remove(neighbour);
                                    openList.add(neighbour);
                                }
                            }
                            // neu da co trong tap dong
                            else if(closedList.contains(neighbour)) {
                                int previousF = f[neighbour.point.x][neighbour.point.y];
                                if(neighbour.f < previousF) {
                                    f[neighbour.point.x][neighbour.point.y] = neighbour.f;
                                    closedList.remove(neighbour);
                                    closedList.add(neighbour);
                                }
                            }
                            // neu khong thi them vao tap mo
                            else {
                                f[neighbour.point.x][neighbour.point.y] = neighbour.f;
                                openList.add(neighbour);
                                publish(neighbour.point);
                                Thread.sleep(delayTime);
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Point> chunks) {
                // Update the GUI with the "flooding cell effect"
                for (Point point : chunks) {
                    mainPanel.getComponent(getRealIndex(point.x, point.y)).setBackground(currentColor);
                }
            }

            @Override
            protected void done() {
                // Re-enable buttons after the algorithm finishes
                mainPanel.getComponent(getRealIndex(start.x, start.y)).setBackground(Color.GREEN);
                mainPanel.getComponent(getRealIndex(goal.x, goal.y)).setBackground(Color.RED);
                btnRun.setEnabled(true);
                btnRenew.setEnabled(true);
                btnRandomMaze.setEnabled(true);
                btnBlockPoint.setEnabled(true);
                btnGoalPoint.setEnabled(true);
                btnReload.setEnabled(true);
                btnStartPoint.setEnabled(true);
            }
        };

        // Start the SwingWorker
        worker.execute();
    }

    // hien thi frame len man hinh
    private void display() {
        setFrame();
        setMenuPanel();
        setMainPanel();
        initStartAndGoal();
        this.setVisible(true);
    }

    // khoi tao frame
    private void setFrame() {
        this.setSize(new Dimension(160+column*buttonSize+10, row*buttonSize));
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
    }

    // khoi tao ban do (map) va cac thanh phan can thiet
    private void setMainPanel() {
        mainPanel = new JPanel(new GridLayout(row, column));
        mainPanel.setBounds(160, 0, this.getWidth()-150-10, this.getHeight());
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                mainPanel.add(createButton());
            }
        }
        this.add(mainPanel);
    }

    // khoi tao bang menu va cac thanh phan cua no
    private void setMenuPanel() {
        menuPanel = new JPanel(new GridLayout(8, 1, 0, 40));
        menuPanel.setBounds(0, 0, 150, this.getHeight());

        btnStartPoint = createMenuButton("Pick the start point");
        btnGoalPoint = createMenuButton("Pick goal point");
        btnBlockPoint = createMenuButton("Pick the blocks");
        btnRun = createMenuButton("Start");
        btnReload = createMenuButton("Reload");
        btnRenew = createMenuButton("Renew");
        btnRandomMaze = createMenuButton("Random maze");
        btnExit = createMenuButton("Exit");

        isStartPointOn = false;
        isGoalPointOn = false;
        isInitPointOn = false;
        defaultBtnColor = btnRun.getBackground();

        menuPanel.add(btnRun);
        menuPanel.add(btnStartPoint);
        menuPanel.add(btnGoalPoint);
        menuPanel.add(btnBlockPoint);
        menuPanel.add(btnRandomMaze);
        menuPanel.add(btnReload);
        menuPanel.add(btnRenew);
        menuPanel.add(btnExit);

        this.add(menuPanel);
    }

    // khoi tao o bat dau va o ket thuc o vi tri mac dich(dau map va cuoi map)
    private void initStartAndGoal() {
        start = new Point(0, 0);
        goal = new Point(row-1, column-1);
        mainPanel.getComponent(getRealIndex(start.x, start.y)).setBackground(Color.GREEN);
        mainPanel.getComponent(getRealIndex(goal.x, goal.y)).setBackground(Color.RED);
    }

    // anh xa tu toa do trong map sang index tuong ung trong main panel
    private int getRealIndex(int x, int y) {
        return x*column+y;
    }

    // anh xa tu index trong main panel sang toa do tuong ung trong map
    private Point getRealPoint(int index) {
        return new Point(index/column, index%column);
    }

    // tao ra cac nut bam trong bang menu
    private JButton createMenuButton(String name) {
        JButton button = new JButton(name);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);
        return button;
    }

    // tao ra mot o trong map (duoi dang mot button)
    private JButton createButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(buttonSize, buttonSize));
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(Color.WHITE);
        button.addActionListener(this);
        return button;
    }

    // xu ly su kien khi click vao nut chon o bat dau
    private void actionOfBtnInitStartPoint() {
        // tat cac nut khac neu dang bat
        if(isGoalPointOn) {
            isGoalPointOn = false;
            btnGoalPoint.setBackground(defaultBtnColor);
        }

        if(isInitPointOn) {
            isInitPointOn = false;
            btnBlockPoint.setBackground(defaultBtnColor);
        }

        if(!isStartPointOn) {
            isStartPointOn = true;
            btnStartPoint.setBackground(Color.GREEN);
        }
        else {
            isStartPointOn = false;
            btnStartPoint.setBackground(defaultBtnColor);
        }
    }

    // xu ly su kien khi click vao nut chon o ket thuc
    private void actionOfBtnInitGoalPoint() {
        // tat cac nut khac neu dang bat
        if(isStartPointOn) {
            isStartPointOn = false;
            btnStartPoint.setBackground(defaultBtnColor);
        }

        if(isInitPointOn) {
            isInitPointOn = false;
            btnBlockPoint.setBackground(defaultBtnColor);
        }

        if(!isGoalPointOn) {
            isGoalPointOn = true;
            btnGoalPoint.setBackground(Color.RED);
        }
        else {
            isGoalPointOn = false;
            btnGoalPoint.setBackground(defaultBtnColor);
        }
    }

    // xu ly su kien khi click vao nut chon o block (chuong ngai vat)
    private void actionOfBtnInitBlock() {
        // tat cac nut khac neu dang bat
        if(isStartPointOn) {
            isStartPointOn = false;
            btnStartPoint.setBackground(defaultBtnColor);
        }

        if(isGoalPointOn) {
            isGoalPointOn = false;
            btnGoalPoint.setBackground(defaultBtnColor);
        }

        if(!isInitPointOn) {
            isInitPointOn = true;
            btnBlockPoint.setBackground(Color.GRAY);
        }
        else {
            isInitPointOn = false;
            btnBlockPoint.setBackground(defaultBtnColor);
        }
    }

    // xu ly su kien khi click vao mot o trong map
    private void actionOfTable(ActionEvent event) {
        for(int index = 0; index < row*column; index++) {
            if(mainPanel.getComponent(index) == event.getSource()) {
                Point point = getRealPoint(index);
                // chon o bat dau
                if(isStartPointOn) {
                    if(!point.equals(goal) && !map[point.x][point.y]) {
                        mainPanel.getComponent(getRealIndex(start.x, start.y)).setBackground(Color.WHITE);
                        mainPanel.getComponent(index).setBackground(Color.GREEN);
                        start = point;
                    }
                }
                // chon o ket thuc
                else if(isGoalPointOn) {
                    if(!point.equals(start) && !map[point.x][point.y]) {
                        mainPanel.getComponent(getRealIndex(goal.x, goal.y)).setBackground(Color.WHITE);
                        mainPanel.getComponent(index).setBackground(Color.RED);
                        goal = point;
                    }
                }
                // chon cac o block (chuong ngai vat)
                else if(isInitPointOn) {
                    if(!point.equals(start) && !point.equals(goal)) {
                        if(!map[point.x][point.y]) {
                            map[point.x][point.y] = true;
                            mainPanel.getComponent(index).setBackground(Color.DARK_GRAY);
                        }
                        else {
                            map[point.x][point.y] = false;
                            mainPanel.getComponent(index).setBackground(Color.WHITE);
                        }
                    }
                }
            }
        }
    }

    // sinh ma tran block (chuong ngai vat) ngau nhien
    private void createRandomMaze() {
        renew();
        for(int i = 0; i < row; i++) {
            int numberOfBlockColumn = (int)Math.round(Math.random()*(column-1));
            for(int j = 0; j < numberOfBlockColumn; j++) {
                int k = (int)Math.round(Math.random()*(column-1));
                Point point = new Point(i, k);
                if(!point.equals(start) && !point.equals(goal)) {
                    map[i][k] = true;
                    mainPanel.getComponent(getRealIndex(i, k)).setBackground(Color.DARK_GRAY);
                }
            }
        }
    }

    // doi mau cac o da duoc mo rong(mau xanh) va cac o duong di (mau vang) thanh mau trang
    private void reload() {
        for(int i = 0; i < row*column; i++) {
            Color color = mainPanel.getComponent(i).getBackground();
            if(color.equals(Color.YELLOW) || color.equals(Color.CYAN)) {
                mainPanel.getComponent(i).setBackground(Color.WHITE);
            }
        }
    }

    // lam moi toan bo map
    private void renew() {
        reload();
        for(int i = 0; i < row*column; i++) {
            Color color = mainPanel.getComponent(i).getBackground();
            if(color.equals(Color.DARK_GRAY)) {
                mainPanel.getComponent(i).setBackground(Color.WHITE);
                Point point = getRealPoint(i);
                map[point.x][point.y] = false;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnStartPoint) {
            actionOfBtnInitStartPoint();
        }
        else if(e.getSource() == btnGoalPoint) {
            actionOfBtnInitGoalPoint();
        }
        else if(e.getSource() == btnBlockPoint) {
            actionOfBtnInitBlock();
        }
        else if(e.getSource() == btnRun) {
            reload();
            aStarAlgorithm();
        }
        else if(e.getSource() == btnReload) {
            reload();
        }
        else if(e.getSource() == btnRenew) {
            renew();
        }
        else if(e.getSource() == btnRandomMaze) {
            createRandomMaze();
        }
        else if(e.getSource() == btnExit) {
            System.exit(0);
        }
        else {
            actionOfTable(e);
        }
    }
}
