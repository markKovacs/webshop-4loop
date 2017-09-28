package com.codecool.shop.utility;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.codecool.shop.Config.*;


public class Log {

    public static void save(String type, String fileName, String Data) throws IOException {

        String FOLDER = "";

        switch (type) {
            case "order":
                FOLDER = ORDER_LOG_FOLDER;
                break;
            case "admin":
                FOLDER = ADMIN_LOG_FOLDER;
                break;
        }


        BufferedWriter bwriter = null;
        FileWriter fwriter = null;

        try {
            File file = new File( FOLDER + "/" + getNowAsString() + "_" + fileName + ".txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            fwriter = new FileWriter(file.getAbsoluteFile(), true);
            bwriter = new BufferedWriter(fwriter);

            bwriter.append(Data + "\n");
        }
        catch (IOException error) {
            System.out.println("Error saving log: " + error);
        }
        finally {

            try {
                if (bwriter != null)
                    bwriter.close();

                if (fwriter != null) {
                    fwriter.close();
                }
            }
            catch (IOException ex) {
                System.out.println("Error closing File: " + ex);
            }

        }

    }

    private static String getNowAsString() {
        SimpleDateFormat formattedDate = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");
        Date now = new Date();
        String strDate = formattedDate.format(now);
        return strDate;
    }


}
