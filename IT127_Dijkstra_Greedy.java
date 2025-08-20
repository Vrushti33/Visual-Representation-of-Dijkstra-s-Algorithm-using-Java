import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class IT127_Dijkstra_Greedy extends Frame implements ActionListener
{
    private TextArea outputArea;
    private TextField inputField;
    private int[][] graph;
    private int vertices;
    private Canvas canvas;
    private int[] dist;
    private boolean[] visited;
    private int[] parent;
    private boolean showFinalGraph = false;

    public IT127_Dijkstra_Greedy(int vertices) 
    {
        this.vertices = vertices;
        this.graph = new int[vertices][vertices];
        this.dist = new int[vertices];
        this.visited = new boolean[vertices];
        this.parent = new int[vertices];

        setTitle("Dijkstra's Algorithm Using Greedy Technique");
        setSize(1000, 600);
        setLayout(new BorderLayout());

        Panel inputPanel = new Panel();
        inputPanel.setLayout(new FlowLayout());
        inputField = new TextField(20);
        Button enterGraphButton = new Button("Enter Graph");
        enterGraphButton.addActionListener(this); 

        inputPanel.add(new Label("Enter adjacency matrix row-wise (semi-colon separated):"));
        inputPanel.add(inputField);
        inputPanel.add(enterGraphButton);
        add(inputPanel, BorderLayout.NORTH);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        add(outputArea, BorderLayout.SOUTH);

        Button computeButton = new Button("Compute Dijkstra");
        computeButton.addActionListener(this); //e -> new Thread(() -> computeDijkstra(0)).start()
        add(computeButton, BorderLayout.EAST);

        canvas = new Canvas() 
        {
            @Override
            public void paint(Graphics g) 
            {
                drawGraph(g);
            }
        };
        canvas.setSize(400, 300);
        add(canvas, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) 
            {
                dispose();
            }
        } );

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand()=="Enter Graph")
            enterGraph();
        
        if(e.getActionCommand()=="Compute Dijkstra")
            new Thread( () -> computeDijkstra(0) ).start();
    }

    private void enterGraph() 
    {
        try 
        {
            String[] rows = inputField.getText().split(";");
            for (int i = 0; i < vertices; i++) 
            {
                String[] values = rows[i].split(",");
                for (int j = 0; j < vertices; j++) 
                {
                    graph[i][j] = Integer.parseInt(values[j]);
                }
            }
            outputArea.setText("Graph adjacency matrix stored successfully.\nClick Compute Dijkstra to proceed.");
        } 
        catch(Exception e) 
        {
            outputArea.setText("Invalid input! Please enter rows as comma-separated values and separate rows with a semicolon.");
        }
    }

    private void computeDijkstra(int src) 
    {
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(visited, false);
        Arrays.fill(parent, -1);
        dist[src] = 0;

        for (int count = 0; count < vertices - 1; count++) 
        {
            int u = minDistance();
            visited[u] = true;
            canvas.repaint();
            try { Thread.sleep(1000); } catch(InterruptedException e) { e.printStackTrace(); }
            
            for (int v = 0; v < vertices; v++) {
                if (!visited[v] && graph[u][v] != 0 && dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                    parent[v] = u;
                }
            }
        }
        displayResults();
        showFinalGraph = true;
        canvas.repaint();
    }

    private int minDistance() 
    {
        int min = Integer.MAX_VALUE, minIndex = -1;
        for (int v = 0; v < vertices; v++) 
        {
            if (!visited[v] && dist[v] <= min) 
            {
                min = dist[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    private void displayResults() 
    {
        StringBuilder sb = new StringBuilder("Shortest distances from source vertex:\n");
        for (int i = 0; i < vertices; i++) 
        {
            sb.append("Vertex ").append(i).append(": ").append(dist[i]).append("\n");
        }
        outputArea.setText(sb.toString());
    }

    private void drawGraph(Graphics g) 
    {
        int radius = 20;
        int centerX = canvas.getWidth() / 2;
        int centerY = canvas.getHeight() / 2;
        int step = 360 / vertices;
        int graphRadius = Math.min(canvas.getWidth(), canvas.getHeight()) / 3;
        Point[] points = new Point[vertices];

        for (int i = 0; i < vertices; i++) 
        {
            int x = (int) (centerX + graphRadius * Math.cos(Math.toRadians(i * step)));
            int y = (int) (centerY + graphRadius * Math.sin(Math.toRadians(i * step)));
            points[i] = new Point(x, y);
        }

        g.setColor(Color.BLACK);
        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                if (graph[i][j] != 0) {
                    if (!showFinalGraph || parent[j] == i || parent[i] == j) {
                        g.drawLine(points[i].x, points[i].y, points[j].x, points[j].y);
                        g.drawString(String.valueOf(graph[i][j]),
                                (points[i].x + points[j].x) / 2,
                                (points[i].y + points[j].y) / 2);
                    }
                }
            }
        }

        for (int i = 0; i < vertices; i++) 
        {
            g.setColor(visited[i] ? Color.GREEN : Color.RED);
            g.fillOval(points[i].x - radius / 2, points[i].y - radius / 2, radius, radius);
            g.setColor(Color.BLACK);
            g.drawOval(points[i].x - radius / 2, points[i].y - radius / 2, radius, radius);
            g.drawString("V" + i, points[i].x - 5, points[i].y + 5);
        }
    }

    public static void main(String[] args) 
    {
        new IT127_Dijkstra_Greedy(6); 
    }
}

//0,7,9,0,0,14;7,0,10,15,0,0;9,10,0,11,0,2;0,15,11,0,6,0;0,0,0,6,0,9;14,0,2,0,9,0 - 6
//0,10,0,0,5;0,0,1,0,2;0,0,0,4,0;7,0,6,0,0;0,3,9,2,0 - 5
//0,10,0,30;10,0,50,0;0,50,0,20;30,0,20,0 - 4