package com.flyingBird.www.util;

import com.flyingBird.www.entity.Grade;

import java.io.*;

public class FileUtil {
    private static final String DATA_FILE = "data.txt";

    public static Grade readFile() {
        Grade grade = new Grade();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line = bufferedReader.readLine();
            if (line != null) {
                String[] dataArray = line.split(",");
                grade.setScore(dataArray[0]);
                grade.setUseTime(dataArray[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grade;
    }

    public static Boolean writeFile(Grade grade) {
        Boolean result = false;
        Grade originGrade = readFile();
        // 更新成绩
        // 2.历史成绩比最新成绩低
        // 3.历史成绩等同于最新成绩，但使用时间更短
        if (originGrade.getScore() != null && originGrade.getUseTime() != null) {
            if (Integer.parseInt(originGrade.getScore()) < Integer.parseInt(grade.getScore())) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(DATA_FILE, false) )) {
                    // false表示清空后重新写入
                    // true表示追加
                    bufferedWriter.write(grade.toString());
                    result = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (Integer.parseInt(originGrade.getScore()) == Integer.parseInt(grade.getScore())) {
                String[] originTimes = originGrade.getUseTime().split(":");
                int originTime = Integer.parseInt(originTimes[0]) * 60 * 60 + Integer.parseInt(originTimes[1]) * 60 + Integer.parseInt(originTimes[2]);

                String[] currentTimes = grade.getUseTime().split(":");
                int currentTime = Integer.parseInt(currentTimes[0]) * 60 * 60 + Integer.parseInt(currentTimes[1]) * 60 + Integer.parseInt(currentTimes[2]);

                if (originTime > currentTime) {
                    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(DATA_FILE, false) )) {
                        // false表示清空后重新写入
                        // true表示追加
                        bufferedWriter.write(grade.toString());
                        result = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {}
        } else {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(DATA_FILE, false) )) {
                // false表示清空后重新写入
                // true表示追加
                bufferedWriter.write(grade.toString());
                result = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return result;
    }
}
