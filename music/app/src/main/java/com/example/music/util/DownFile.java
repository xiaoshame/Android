package com.example.music.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by xiaozhisong on 15-1-26.
 */
public class DownFile {
    public static void DownFileToPath(Context content,InputStream in){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;
        try{
            out = content.openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null){
                writer.write(line);
            }
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
