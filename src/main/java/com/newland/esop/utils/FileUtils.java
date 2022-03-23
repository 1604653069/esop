package com.newland.esop.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {
    /**
     * 删除空目录
     * @param dir 将要删除的目录路径
     */
    public static void doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("Successfully deleted empty directory: " + dir);
        } else {
            System.out.println("Failed to delete empty directory: " + dir);
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     *  复制单个文件
     *  @param  oldPath  String  原文件路径  如：c:/fqf.txt
     *  @param  newPath  String  复制后路径  如：f:/fqf.txt
     *  @return  boolean
     */
    public  static void  copyFile(String  oldPath,  String  newPath)  {
        try  {
            int  byteread  =  0;
            File  oldfile  =  new  File(oldPath);
            if  (oldfile.exists())  {  //文件存在时
                InputStream inStream  =  new FileInputStream(oldPath);  //读入原文件
                FileOutputStream  fs  =  new FileOutputStream(newPath);
                byte[]  buffer  =  new  byte[1444];
                while  (  (byteread  =  inStream.read(buffer))  !=  -1)  {
                    fs.write(buffer,  0,  byteread);
                }
                inStream.close();
            }
        }
        catch  (Exception  e)  {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }
}
