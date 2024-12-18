import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class DrawMap extends JPanel {
    private Node[] nodes;
    private Timer timer;
    private String FilePath;
    private Image backgroundImage;
    public DrawMap(Node[] nodes,String FilePath) {
        this.nodes = nodes;
        this.FilePath = FilePath;
        this.backgroundImage = new ImageIcon("mapbg2.png").getImage(); // 替换为实际的图片路径
        setupTimer();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 开启抗锯齿
        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        for (Node node : nodes) {
            int x = node.x * 50; // 坐标放大倍数，用于在画布上显示
            int y = node.y * 50;

            // 绘制节点
            if (node.type.equals("CulturalSpot")) {
                g2d.setColor(new Color(60, 179, 113)); // 修改为海绿色
                g2d.fillOval(x, y, 20, 20);
            } else if (node.type.equals("SchoolBuilding")) {
                g2d.setColor(new Color(100, 149, 237)); // 修改为淡蓝色
                g2d.fillOval(x, y, 20, 20);
            } else if (node.type.equals("dormitory")) {
                g2d.setColor(new Color(255, 165, 0)); // 修改为橙色
                g2d.fillOval(x, y, 20, 20);
            }else{
                g2d.setColor(new Color(255, 255, 153)); // 修改为浅黄色
                g2d.fillOval(x, y, 20, 20);
            }

            g2d.setColor(Color.BLACK);
            g2d.drawString(node.name, x + 25, y + 25);

            // 绘制边
            if (node.edge_name != null) {
                g2d.setStroke(new BasicStroke(2)); // 设置线条粗细
                for (Node node_edge : nodes) {
                    for(int i = 0; i < node.edge_name.size();i++) {
                        if (node_edge.name.equals(node.edge_name.get(i))) {
                            int startX = node.x * 50 + 10; // 起始点x坐标
                            int startY = node.y * 50 + 10; // 起始点y坐标
                            int endX = node_edge.x * 50 + 10; // 结束点x坐标
                            int endY = node_edge.y * 50 + 10; // 结束点y坐标
                            g2d.setColor(new Color(255, 105, 180)); // 修改为粉红色
                            g2d.drawLine(startX, startY, endX, endY);
                        }
                    }
                }
            } else if (node.edge_name == null) {
                System.out.println("wrong");
            }
        }

        // 添加图例
        drawLegend(g2d);
    }

    private void drawLegend(Graphics2D g2d) {
        int legendX = 20; // 标注区域起始点x坐标
        int legendY = 20; // 标注区域起始点y坐标

        g2d.setColor(Color.BLACK);
        g2d.drawString("Legend:", legendX, legendY);

        int legendItemY = legendY + 20; // 标注项起始点y坐标

        g2d.setColor(new Color(60, 179, 113)); // CulturalSpot 海绿色
        g2d.fillOval(legendX, legendItemY, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("CulturalSpot", legendX + 20, legendItemY + 12);

        legendItemY += 30;
        g2d.setColor(new Color(100, 149, 237)); // SchoolBuilding 淡蓝色
        g2d.fillOval(legendX, legendItemY, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("SchoolBuilding", legendX + 20, legendItemY + 12);

        legendItemY += 30;
        g2d.setColor(new Color(255, 165, 0)); // dormitory 橙色
        g2d.fillOval(legendX, legendItemY, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("dormitory", legendX + 20, legendItemY + 12);

        legendItemY += 30;
        g2d.setColor(new Color(255, 255, 153)); // 修改为浅黄色
        g2d.fillOval(legendX, legendItemY, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawString("other", legendX + 20, legendItemY + 12);

    }

    public static Node[] readNodesFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<Node> nodeList = new ArrayList<>();
        String line;
        int lineCount = 0; // 行号计数器
        while ((line = reader.readLine()) != null) {
            lineCount++;
            if (lineCount == 1) {
                continue; // 跳过第一行
            }
            String[] fields = line.split("\\s+");
            int id = Integer.parseInt(fields[0]);
            String name = fields[1];
            int x = Integer.parseInt(fields[2]);
            int y = Integer.parseInt(fields[3]);
            List<String> edge_name = new ArrayList<String>();
            for (int i = 4; i < fields.length - 1; i++){
                edge_name.add(fields[i]);
            }
            String type = fields[fields.length - 1].trim();
            Node node = new Node(id, x, y, name, type, edge_name);
            nodeList.add(node);
        }
        reader.close();
        return nodeList.toArray(new Node[0]);
    }

    public static class Node {
        public int id;
        public int x;
        public int y;
        public String name;
        public String type;
        public List<String> edge_name;

        public Node(int id, int x, int y, String name, String type, List<String> edge_name) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.name = name;
            this.type = type;
            this.edge_name = edge_name;
        }
    }

    private void setupTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                // 从文件重新加载节点
                try {
                    nodes = readNodesFromFile(FilePath);
                    repaint(); // 重新绘制面板
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3000); // 每3秒刷新一次
    }

    public static void main(String[] args) {
        try {
            DrawMap.Node[] nodes = DrawMap.readNodesFromFile("D:\\javademo\\mymap\\src\\campus_map.txt");
            JFrame frame = new JFrame("Map");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 600);
            frame.setLocationRelativeTo(null);
            DrawMap map = new DrawMap(nodes,"D:\\javademo\\mymap\\src\\campus_map.txt");
            frame.add(map);

            frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}