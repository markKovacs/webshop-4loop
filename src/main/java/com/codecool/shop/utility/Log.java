package com.codecool.shop.utility;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.codecool.shop.Config.*;

public class Log {

    public static void saveActionToOrderLog(String fileName, String action){
        String Data = getNowAsString();
        switch (action) {
            case "reviewed": Data += " : Order has been reviewed."; break;
            case "checkedout": Data += " : Order has been checked out."; break;
            case "paid"    : Data += " : Order has been paid."; break;
        }
        save(ADMIN_LOG_FOLDER, fileName, Data);
    }

    public static void saveOrderToJson(int orderId, String data){
        String fileName = getNowAsString() + "_" + orderId + "_order";
        save(ORDER_LOG_FOLDER, fileName, data);
    }

    private static void save(String folder, String fileName, String Data) {
        BufferedWriter bWriter = null;
        FileWriter fWriter = null;
        try {
            File file = new File( folder + "/" + fileName + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            fWriter = new FileWriter(file.getAbsoluteFile(), true);
            bWriter = new BufferedWriter(fWriter);

            bWriter.append(Data + "\n");
        }
        catch (IOException error) {
            System.out.println("Error saving log: " + error);
        }
        finally {
            try {
                if (bWriter != null)
                    bWriter.close();

                if (fWriter != null) {
                    fWriter.close();
                }
            }
            catch (IOException ex) {
                System.out.println("Error closing File: " + ex);
            }
        }
    }

    public static String getNowAsString() {
        SimpleDateFormat formattedDate = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");
        Date now = new Date();
        String strDate = formattedDate.format(now);
        return strDate;
    }

}
