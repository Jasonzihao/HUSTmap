import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextFileEditor {

    private String filePath;

    public TextFileEditor(String filePath) {
        this.filePath = filePath;
    }

    public int addLine(String newContent) {
        int NewId = 0;
        ArrayList<String> lines = new ArrayList<>();
        //检验格式
        String[] parts = newContent.split("\\s+") ;
        try {
            NewId = Integer.parseInt(parts[0]);
        }catch (Exception e){
            return 0;
        }

        if(parts.length >= 6){
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                line = reader.readLine();

                //对文件首部的总数进行处理
                int TotalLineNum = Integer.parseInt(line);
                int NewTotalLineNum = TotalLineNum + 1;
                line = Integer.toString(NewTotalLineNum);
                if(NewTotalLineNum != NewId){
                    return 2;
                }
                lines.add(line);

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
             }
        //写入新信息
            try (FileWriter writer = new FileWriter(filePath, true)) {
                writer.write(newContent + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }
        else{
            return 0;
        }
    }

    public int deleteLine(int ID) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            String[] parts = line.split("\\s+", 2);
            int TotalLineNum = Integer.parseInt(parts[0]);
            if(ID>TotalLineNum || ID<= 0){
                return 0;
            }
        }catch (IOException e){
            e.printStackTrace();
        }


        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 0;
            while ((line = reader.readLine()) != null) {
                if(currentLine == 0){
                    int linenum = Integer.parseInt(line);
                    int totalline = linenum - 1;
                    line = Integer.toString(totalline);
                    lines.add(line);
                }
                if (currentLine < ID && currentLine!=0) {
                    lines.add(line);
                }
                if(currentLine > ID){
                    String[] parts = line.split("\\s+", 2);
                    int id=Integer.parseInt(parts[0])-1;
                    for(int i=1;i<parts.length;i++){
                        line = id + " " + parts[i];
                    }
                    lines.add(line);
                }
                currentLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int editLine(int ID, String newContent) {
        ArrayList<String> lines = new ArrayList<>();
        String[] parts = newContent.split("\\s+") ;
        int id = 0;
        try {
            id = Integer.parseInt(parts[0]);
        }catch (Exception e){
            return 0;
        }

        if(parts.length >= 6 && id==ID){
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                int currentLine = 0;
                while ((line = reader.readLine()) != null) {
                    if (currentLine == ID) {
                        lines.add(newContent);
                    } else {
                        lines.add(line);
                    }
                    currentLine++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        }
        else {
            return 0;
        }
    }

    public List<String> readLines() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void main(String[] args) {
        // 示例用法
        TextFileEditor editor = new TextFileEditor("map.txt");

        // 增加行
        editor.addLine("2 自动化学院 7 9 信息中心 NewSchoolBuilding");

        // 删除行（假设要删除第一行，从0开始计数）
        editor.deleteLine(0);

        // 修改行（假设要修改第二行，从0开始计数）
        editor.editLine(1, "2 自动化学院 8 10 信息中心 ModifiedSchoolBuilding");

        // 读取所有行
        List<String> allLines = editor.readLines();
        for (String line : allLines) {
            System.out.println(line);
        }
    }
}

