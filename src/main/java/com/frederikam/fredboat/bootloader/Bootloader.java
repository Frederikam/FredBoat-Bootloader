package com.frederikam.fredboat.bootloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Bootloader {

    public static JSONArray command;
    public static String jarName;

    public static void main(String[] args) throws IOException, InterruptedException {
        OUTER:
        while (true) {
            InputStream is = new FileInputStream(new File("./bootloader.json"));
            Scanner scanner = new Scanner(is);
            JSONObject json = new JSONObject(scanner.useDelimiter("\\A").next());
            scanner.close();
            command = json.getJSONArray("command");
            jarName = json.getString("jarName");

            Process process = boot();
            process.waitFor();
            System.out.println("[BOOTLOADER] Bot exited with code " + process.exitValue());

            switch (process.exitValue()) {
                case ExitCodes.EXIT_CODE_UPDATE:
                    System.out.println("[BOOTLOADER] Now updating...");
                    update();
                    break;
                case 130:
                case ExitCodes.EXIT_CODE_NORMAL:
                    System.out.println("[BOOTLOADER] Now shutting down...");
                    break OUTER;
                //SIGINT received or clean exit
                default:
                    System.out.println("[BOOTLOADER] Now restarting..");
                    break;
            }
        }
    }

    public static Process boot() throws IOException {
        //ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + "/bin/java -jar "+new File("FredBoat-1.0.jar").getAbsolutePath())
        ProcessBuilder pb = new ProcessBuilder()
                .inheritIO();
        ArrayList<String> list = new ArrayList<>();
        command.forEach((Object str) -> {
            list.add((String) str);
        });

        pb.command(list);

        Process process = pb.start();
        return process;
    }

    public static void update() {
        //The main program has already prepared the shaded jar. We just need to replace the jars.
        File oldJar = new File("./" + jarName);
        oldJar.delete();
        File newJar = new File("./update/target/" + jarName);
        newJar.renameTo(oldJar);

        //Now clean up the workspace
        boolean deleted = new File("./update").delete();
        System.out.println("[BOOTLOADER] Updated. Update dir deleted: " + deleted);
    }

}
