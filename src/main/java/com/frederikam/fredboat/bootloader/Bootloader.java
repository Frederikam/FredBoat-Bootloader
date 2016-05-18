package com.frederikam.fredboat.bootloader;

import java.io.File;
import java.io.IOException;

public class Bootloader {

    public static void main(String[] args) throws IOException, InterruptedException {
        while (true) {
            Process process = boot();
            process.waitFor();

            System.out.println("[BOOTLOADER] Bot exited with code " + process.exitValue());

            if (process.exitValue() == ExitCodes.EXIT_CODE_UPDATE) {
                System.out.println("[BOOTLOADER] Now updating...");
                update();
            } else if (process.exitValue() == ExitCodes.EXIT_CODE_RESTART) {
                System.out.println("[BOOTLOADER] Now restarting..");
                //Continue
            } else {
                System.out.println("[BOOTLOADER] Now shutting down...");
                break;
            }
        }
    }

    public static Process boot() throws IOException {
        //ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + "/bin/java -jar "+new File("FredBoat-1.0.jar").getAbsolutePath())
        ProcessBuilder pb = new ProcessBuilder()
                .command("java", "-jar", "FredBoat-1.0.jar")
                .inheritIO();
        Process process = pb.start();
        return process;
    }

    public static void update() {
        //The main program has already prepared the shaded jar. We just need to replace the jars.
        File oldJar = new File("./FredBoat-1.0.jar");
        oldJar.delete();
        File newJar = new File("./update/target/FredBoat-1.0.jar");
        newJar.renameTo(oldJar);

        //Now clean up the workspace
        new File("./update").delete();
        System.out.println("[BOOTLOADER] Updated");
    }

}
