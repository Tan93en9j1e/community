package com.tang.community;


public class WkTests {
    public static void main(String[] args) {
        String cmd = "E:/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com E:/project/workspace/images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}