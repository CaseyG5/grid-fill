package gridfill;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.EventQueue;
import edu.princeton.cs.algs4.Queue;

class Painter extends JFrame implements ActionListener,
        MouseListener {
    public final int WIDTH = 500;
    public final int HEIGHT = 600;
    public final int X;
    
    private Color ocean;
    private Color sand;
    
    private Color clrSelected;
    private Color old;
    private String id;
    private int cell;
    private Queue<Integer> q;               // queue for BFS
    
    private ColorPanel[][] canvas;          // canvas of clickable panels
    private int[][] adj;                    // ragged array of cells and neighbors
    
    private boolean fill;                   // to either fill or draw
    
    private JPanel colors;
    private JPanel center;
    private JPanel options;
    
    Painter(int size) {
        setTitle("Grid-Fill");
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());
        
        X = size;
        
        ocean = new Color(90, 180, 250);
        sand = new Color(255, 218, 104);
  
        clrSelected = ocean;
        id = "0";
        fill = true;
        
        center = new JPanel();
        center.setLayout(new GridLayout(size, size));
        
        canvas = new ColorPanel[size][size];
        adj = new int[size * size][];
        q = new Queue<>();
        
        prepNeighbors(size, size);
        initPanels(size);
        
        add(center, BorderLayout.CENTER);
        
        colors = new JPanel();
        colors.setBackground(Color.LIGHT_GRAY);
        colors.setLayout(new FlowLayout());
        
        // make buttons for colors 
        JButton blue = new JButton("Ocean");
        blue.addActionListener(this);
        colors.add(blue);
        
        JButton yellow = new JButton("Sand");
        yellow.addActionListener(this);
        colors.add(yellow);
        
        options = new JPanel();
        options.setBackground(Color.LIGHT_GRAY);
        options.setLayout(new FlowLayout());
        
        // buttons for options
        JButton pencil = new JButton("Draw");
        pencil.addActionListener(this);
        options.add(pencil);
            
        JButton bucket = new JButton("Fill");
        bucket.addActionListener(this);
        options.add(bucket);
        
        add(colors, BorderLayout.NORTH);
        add(options, BorderLayout.SOUTH);
    }
    
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch(command) {
            case "Ocean":
                clrSelected = ocean;
                id = "0";
                break;
            case "Sand":
                clrSelected = sand;
                id = "1";
                break;
                
            case "Draw":
                fill = false;
                break;
            case "Fill":
                fill = true;
                break;
            default: 
                System.out.println("an unexpected error occurred");
        }
    }
    
    public void mouseClicked(MouseEvent me) {
        
        ColorPanel temp = (ColorPanel) me.getComponent();
         // if color of clicked panel is not already the selected color
        if(!temp.getBackground().equals(clrSelected)) {
            if(fill)            
                bfsColor(temp.row, temp.col);   // fill or color a single panel
            else {
                canvas[temp.row][temp.col].setBackground(clrSelected);
                canvas[temp.row][temp.col].label.setText(id);
            }
        }
    }
    
    public void mousePressed(MouseEvent me) { }
    public void mouseReleased(MouseEvent me) { }
    public void mouseEntered(MouseEvent me) { }
    public void mouseExited(MouseEvent me) { }
    
    
    // uses BFS to fill adjacent squares of the same color
    private void bfsColor(int r, int c) {
        old = canvas[r][c].getBackground();           // grab previous color
        canvas[r][c].setBackground(clrSelected);      // mark panel with new color
        canvas[r][c].label.setText(id);               // and '0' or '1'
        
        cell = r * X + c;                             // get cell # from row/col
        
        q.enqueue(cell);
        while(!q.isEmpty()) {
            cell = q.dequeue();
            for( int other : adj[cell] ) {            // for each neighbor...
                int row = other / X;                  // extract row/col
                int col = other % X;
                 // if color is old color, change to new color and update label
                if(canvas[row][col].getBackground().equals(old)) {
                    canvas[row][col].setBackground(clrSelected);    
                    canvas[row][col].label.setText(id);
                    q.enqueue(other);                       // add to queue
                }
            }
        }
    }
    
     // initializes panels to either yellow or blue
    private void initPanels(int x) {
        for(short i=0; i<x; i++)
            for(short j=0; j<x; j++) {
                
                canvas[i][j] = new ColorPanel(i, j, id);    // create panel
                
                if(Math.random() < 0.35) {                  // with a color
                    canvas[i][j].setBackground(sand);
                    canvas[i][j].label.setText("1");        // and a label
                }   
                else canvas[i][j].setBackground(ocean);
                    
                canvas[i][j].addMouseListener(this);        
                center.add(canvas[i][j]);
            }
                
            
    }
    
     // prepares a table of cells and their adjacent cells (N,S,E,W)
    private void prepNeighbors(int m, int n) {
        System.out.print("\nPreparing proximity table...");
        
        // Corner values for reference
        // A = 0
        final int B = m - 1;
        final int C = m * (n - 1);
        final int D = m * n - 1;
        
        // Corners and their adjacents
        adj[0] = new int[] { 1, m };
        adj[B] = new int[] { B-1, B+m };
        adj[C] = new int[] { C-m, C+1 };
        adj[D] = new int[] { D-1, D-m };
        
        // Neighbors of TOP row of cells
        for(int i=1; i<B; i++) 
            adj[i] = new int[] { i-1, i+1, i+m };
            
        // Neighbors of BOTTOM row of cells
        for(int i=C+1; i<D; i++) 
            adj[i] = new int[] { i-m, i-1, i+1 };
            
        // Neighbors of LEFT column of cells
        for(int i=m; i<C; i+=m) 
            adj[i] = new int[] { i-m, i+1, i+m };
            
        // Neighbors of RIGHT column of cells
        for(int i=B+m; i<D; i+=m) 
            adj[i] = new int[] { i-m, i-1, i+m };
            
        // Neighbors of all other cells
        for(int i=m; i<C; i+=m)
            for(int j=1; j<B; j++) 
                adj[i+j] = new int[] { i+j-m, i+j-1, i+j+1, i+j+m };
                
        System.out.println("done.");
    }
    
}

 // grid is comprised of these color panels
class ColorPanel extends JPanel {
    int row, col;
    JLabel label;
    
    ColorPanel(int r, int c, String s) {   
        super(); 
        row = r; col = c; 
        label = new JLabel(s);
        add(label);
    }
}


public class GridFill {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Painter p = new Painter(25);
                p.setVisible(true);
                p.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }
}
