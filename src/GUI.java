import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

public class GUI {
    private static final String FilePath = "campus_map.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("校园地图应用");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        Font customFont = new Font("阿里妈妈刀隶体", Font.BOLD, 30);
        Font customFont2 = new Font("正楷", Font.BOLD, 16);

        // 添加欢迎文本框
        JLabel welcomeLabel = new JLabel("校园地图系统", JLabel.CENTER);
        welcomeLabel.setFont(customFont);
        frame.add(welcomeLabel, BorderLayout.NORTH);

        ImageIcon imageIcon = createImageIcon("bg.png"); // 替换为你的图片路径
        if (imageIcon != null) {
            JLabel imageLabel = new JLabel(imageIcon, JLabel.CENTER);
            frame.add(imageLabel, BorderLayout.CENTER);
        }

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));

        JButton mode1Button = new JButton("模式一");
        mode1Button.setFont(customFont2);
        mode1Button.setPreferredSize(new Dimension(0, 40)); // 设置按钮高度
        JButton mode2Button = new JButton("模式二");
        mode2Button.setFont(customFont2);
        JButton mapEditButton = new JButton("地图编辑");
        mapEditButton.setFont(customFont2);
        JButton campusMapButton = new JButton("校园地图");
        campusMapButton.setFont(customFont2);

        buttonPanel.add(mode1Button);
        buttonPanel.add(mode2Button);
        buttonPanel.add(mapEditButton);
        buttonPanel.add(campusMapButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        mode1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMode1Window();
            }
        });

        mode2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMode2Window();
            }
        });

        mapEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMapEditWindow();
            }
        });

        campusMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCampusMapWindow();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private static void showMode1Window() {
        JFrame mode1Frame = new JFrame("模式一");
        mode1Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mode1Frame.setSize(500, 200);
        Font customFont2 = new Font("正楷", Font.BOLD, 16);

        JLabel startLabel = new JLabel("我的位置:");
        startLabel.setFont(customFont2);
        JTextField currentLocationField = new JTextField(20);
        JButton queryButton = new JButton("查询");
        queryButton.setFont(customFont2);
        queryButton.setPreferredSize(new Dimension(0, 50)); // 设置按钮高度

        JTextArea resultArea = new JTextArea(5,20);
        resultArea.setLineWrap(true); // 启用自动换行
        resultArea.setEditable(false);

        mode1Frame.setLayout(new GridLayout(2,1));

        mode1Frame.add(startLabel);
        mode1Frame.add(currentLocationField);
        mode1Frame.add(queryButton);
        mode1Frame.add(new JScrollPane(resultArea));


        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String startLocation = currentLocationField.getText();

                CampusMap campusMap = FileHandler.loadMapFromFile(FilePath);
                System.out.println(campusMap.adjacencyMatrix);

                // 解决问题1
                assert campusMap != null;
                Location start1 = campusMap.getLocationByName(startLocation);
                java.util.List<String> shortestPath1output = new ArrayList<>();

                if (start1.name.equals("error")) {
                    shortestPath1output.add("你输入的地址不存在");
                } else {
                    List<Location> shortestPath1 = ShortestPathAlgorithm.findShortestPath1(campusMap, start1);
                    // 输出结果
                    for (Location location : shortestPath1) {
                            shortestPath1output.add(location.name);
                    }
                    System.out.println(shortestPath1output);
                }
                String result = "Shortest Path 1: " + shortestPath1output;
                resultArea.setText(result);
            }
        });
        mode1Frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        mode1Frame.setVisible(true);
    }

    private static void showMode2Window() {
        JFrame mode2Frame = new JFrame("模式二");
        mode2Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mode2Frame.setSize(500, 200);

        JLabel startLabel = new JLabel("我的位置:");
        JLabel destinationLabel = new JLabel("目的地:");
        JTextField currentLocationField = new JTextField();
        JTextField destinationField = new JTextField();
        JButton queryButton = new JButton("查询");
        JTextField resultField = new JTextField();

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new GridLayout(2,1));
        outputPanel.add(queryButton);
        outputPanel.add(resultField);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(startLabel);
        inputPanel.add(currentLocationField);
        inputPanel.add(destinationLabel);
        inputPanel.add(destinationField);

        mode2Frame.setLayout(new GridLayout(2, 1));

        mode2Frame.add(inputPanel);
        mode2Frame.add(outputPanel);

        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String startLocation = currentLocationField.getText();
                String destination = destinationField.getText();


                CampusMap campusMap = FileHandler.loadMapFromFile(FilePath);

                // 解决问题1
                Location start1 = campusMap.getLocationByName(startLocation);
                Location end1 = campusMap.getLocationByName(destination);
                List<String> shortestPath2output = new ArrayList<>();

                if (start1.name.equals("error") || end1.name.equals("error")) {
                    shortestPath2output.add("你输入的地址不存在");
                } else {
                    List<Location> shortestPath2 = ShortestPathAlgorithm.findShortestPathWithWaypoint(campusMap, start1, end1);
                    // 输出结果
                    for (Location location : shortestPath2) {
                        shortestPath2output.add(location.name);
                    }
                    System.out.println(shortestPath2);
                }
                String result = "Shortest Path 2: " + shortestPath2output;
                resultField.setText(result);
            }
        });
        mode2Frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        mode2Frame.setVisible(true);
    }

    private static void showMapEditWindow() {

        JFrame showMapEdit = new JFrame("地图编辑");
        showMapEdit.setSize(500, 500);
        showMapEdit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        showMapEdit.setLocationRelativeTo(null);

        // 初始化组件
        JTextArea mapTextArea = new JTextArea();
        mapTextArea.setEditable(false);

        JButton addLocationButton = new JButton("增加地点");
        JButton editLocationButton = new JButton("修改地点");
        JButton deleteLocationButton = new JButton("删除地点");

        // 设置布局
        showMapEdit.setLayout(new BorderLayout());

        // 左侧面板
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(new JScrollPane(mapTextArea), BorderLayout.CENTER);

        // 右侧按钮面板
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(3, 1));
        rightPanel.add(addLocationButton);
        rightPanel.add(editLocationButton);
        rightPanel.add(deleteLocationButton);

        // 添加组件到主窗口
        showMapEdit.add(leftPanel, BorderLayout.CENTER);
        showMapEdit.add(rightPanel, BorderLayout.EAST);

        // 读取地图数据
        try (BufferedReader reader = new BufferedReader(new FileReader(FilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                mapTextArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                refreshMapData(mapTextArea);
            }
        }, 0, 5000);



        // 设置按钮点击事件
        addLocationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame addLocation = new JFrame("增加地点");
                addLocation.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                addLocation.setSize(500, 150);

                JLabel addLocationLable = new JLabel("请输入增加地点:");

                JTextField addLocationText = new JTextField();

                JButton queryButton = new JButton("确定");
                JTextField resultField = new JTextField();

                addLocation.setLayout(new GridLayout(2, 1));

                addLocation.add(addLocationLable);
                addLocation.add(addLocationText);
                addLocation.add(queryButton);
                addLocation.add(resultField);

                queryButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String newContent = addLocationText.getText();
                        TextFileEditor Editor = new TextFileEditor(FilePath);
                        int flag = Editor.addLine(newContent);
                        if (flag == 1) {
                            resultField.setText(newContent + "增加成功!");
                        } else if (flag == 2) {
                            resultField.setText("ID有误，请重试！！！");
                        } else {
                            resultField.setText("输入格式错误，请重试！！！");
                        }
                    }
                });

                addLocation.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                addLocation.setVisible(true);
            }
        });

        editLocationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame editLocation = new JFrame("修改地点");
                editLocation.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                editLocation.setSize(500, 250);

                JLabel editLocationLable = new JLabel("请输入修改地点ID");
                JLabel editLocationLable2 = new JLabel("请输入修改后的文本");

                JTextField editLocationText = new JTextField();
                JTextField editLocationText2 = new JTextField();

                JButton queryButton = new JButton("确定");
                JTextField resultField = new JTextField();

                JPanel outputPanel = new JPanel();
                outputPanel.setLayout(new GridLayout(2,1));
                outputPanel.add(queryButton);
                outputPanel.add(resultField);

                JPanel inputPanel = new JPanel(new GridLayout(2, 2));
                inputPanel.add(editLocationLable);
                inputPanel.add(editLocationText);
                inputPanel.add(editLocationLable2);
                inputPanel.add(editLocationText2);

                editLocation.setLayout(new GridLayout(2, 1));

                editLocation.add(inputPanel);
                editLocation.add(outputPanel);

                queryButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int ID = 0;
                        TextFileEditor Editor = new TextFileEditor(FilePath);
                        try {
                            ID = Integer.parseInt(editLocationText.getText());
                        }catch (Exception exception){
                            System.out.println(exception.toString());
                        }

                        String newContent = editLocationText2.getText();

                        int Result = Editor.editLine(ID, newContent);
                        if (Result == 0) {
                            String error = "修改格式出错";
                            resultField.setText(error);
                        } else {
                            String success = "修改成功，已经修改为" + newContent;
                            resultField.setText(success);
                        }
                    }
                });
                editLocation.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                editLocation.setVisible(true);
            }
        });

        deleteLocationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame deleteLocation = new JFrame("删除地点");
                deleteLocation.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                deleteLocation.setSize(500, 150);

                JLabel deleteLocationLable = new JLabel("请输入删除地点ID");

                JTextField deleteLocationText = new JTextField();

                JButton queryButton = new JButton("确定");
                JTextField resultField = new JTextField();

                deleteLocation.setLayout(new GridLayout(2, 1));

                deleteLocation.add(deleteLocationLable);
                deleteLocation.add(deleteLocationText);
                deleteLocation.add(queryButton);
                deleteLocation.add(resultField);

                queryButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int id = 0;
                        TextFileEditor Editor = new TextFileEditor(FilePath);
                        try{
                            id = Integer.parseInt(deleteLocationText.getText());
                        }catch (Exception exception){
                            System.out.println(exception.toString());
                        }
                        int flag = Editor.deleteLine(id);
                        if (flag == 1) {
                            resultField.setText("成功删除ID为" + id + "的地点");
                        } else {
                            resultField.setText("输入ID不存在！");
                        }
                    }
                });
                deleteLocation.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                deleteLocation.setVisible(true);
            }
        });
        showMapEdit.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        showMapEdit.setVisible(true);
    }


    private static void showCampusMapWindow() {
        try {
            DrawMap.Node[] nodes = DrawMap.readNodesFromFile(FilePath);
            JFrame frame = new JFrame("Map");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 600);
            frame.setLocationRelativeTo(null);
            DrawMap map = new DrawMap(nodes,FilePath);
            frame.add(map);

            frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static ImageIcon createImageIcon(String path) {
        URL imgURL = GUI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    private static void refreshMapData(JTextArea mapTextArea) {
        // 在这里重新读取地图数据并更新 JTextArea
        try (BufferedReader reader = new BufferedReader(new FileReader(FilePath))) {
            StringBuilder newText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                newText.append(line).append("\n");
            }
            mapTextArea.setText(newText.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


