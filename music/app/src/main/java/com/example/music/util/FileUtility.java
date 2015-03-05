package com.example.music.util;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by xiaozhisong on 15-1-29.
 */
public class FileUtility{
    //sd卡的根路径
    private String sdPath;

    public FileUtility(){
        sdPath = Environment.getExternalStorageDirectory() + "/";
    }

    public String getsdPath(){
        return sdPath;
    }

    public static boolean isFileExists(String path){
        File file = new File(path);
        return file.exists();
    }
    //在sd卡上创建目录,并返回创建的完整路径
    public synchronized String createSDDir(String dirName){
        File dir = new File(sdPath + dirName);
        //文件夹不存在
        if(!dir.exists()){
            dir.mkdirs();
        }
        return sdPath + dirName;
    }

    //创建指定文件
    public File createSDFile(String fileName) throws IOException {
        File file = new File(sdPath + fileName);
        file.createNewFile();
        return file;
    }

    public void changeFileName(String oldName,String newName){
        File file = new File(oldName);
        file.renameTo(new File(newName));
    }
    //将指定数据写入到本地指定文件
    public void WriteToSdPath(String content,String path,String fileName){
        File file = null;
        BufferedWriter writer = null;
        try{
            //创建指定目录
            createSDDir(path);
            //创建指定文件
            file = createSDFile(path + "/" + fileName);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(content);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(writer != null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
