package com.integrity;

import org.apache.spark.SparkConf;
import org.apache.spark.scheduler.SparkListener;
import org.apache.spark.scheduler.SparkListenerJobEnd;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;
import scala.Unit;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Component
public class SparkSessionRunner {
    public SparkSession sparkSession;

    SparkSessionRunner(){
        runSession();
        sparkSession = this.getSparkSession();
    }

    public SparkSession getSparkSession(){
        return sparkSession;
    }

    public void runSession() {
//        String jarPath = "C:\\IdeaProjects\\IntegrityInstruments\\uploads\\"; //TODO put it in properties
        String jars = "";// updateSparkJars(jarPath);
        try {
            sparkSession = SparkSession.builder()
                    .appName("Main")
                    .config("spark.master", "local[16]")
//                    .config("spark.jars", "C:\\IdeaProjects\\IntegrityInstruments\\uploads\\IntegrityAlgorithm-1.3-SNAPSHOT.jar") //TODO put into properties
                    .config("spark.jars", jars)
                    .config("spark.worker.instances", "4")
                    .config("spark.executor.cores", "16")
                    .config("spark.executor.memory", "4g")  // Set executor memory to 4 gigabytes
                    .config("spark.driver.memory", "2g")
                    .getOrCreate(); //local[*] ////localhost:7077 seems not working
            sparkSession.sparkContext().setLogLevel("ERROR");

//            SparkConf sconf = new SparkConf()
//                    .set("spark.eventLog.dir", "hdfs://nn:8020/user/spark/applicationHistory")
//                    .set("spark.eventLog.enabled", "true")
//                    .setJars(new String[]{"/path/to/jar/with/your/class.jar"})
//                    .setMaster("spark://spark.standalone.uri:7077");

            sparkSession.sparkContext().addSparkListener(new SparkListener() {
                @Override
                public void onJobEnd(SparkListenerJobEnd jobEnd) {
                    long jobDuration = jobEnd.time();
                }
             });
             System.out.println("Spark listener created");

            System.out.println("Spark context created");

//            Viewer.view();
//            System.out.println("Viewer created");

        } catch (Exception e) {
            System.out.println("Error: Cannot create SparkContext");
            e.printStackTrace();
        }
    }

/*
    private String updateSparkJars(String folderPath) {
        try {
            // List all JAR files in the specified folder
            Set<String> jarPaths = listJarsInFolder(folderPath);

            // Get the existing JAR paths as a set
            Set<String> existingJarPaths = new HashSet<>();

            // Add new paths to the set
            existingJarPaths.addAll(jarPaths);

            // Convert the set back to a comma-separated string
            String updatedJars = String.join(",", existingJarPaths);

            return updatedJars;
            // Now, sparkSession will use the updated set of JAR paths
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
*/

/*    private static Set<String> listJarsInFolder(String folderPath) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath), "*.jar")) {
            Set<String> jarPaths = new HashSet<>();
            for (Path entry : stream) {
                jarPaths.add(entry.toString());
            }
            return jarPaths;
        }
    }*/
    public void stopSession(SparkSession spark) {
        // Add a delay or user input to keep the Spark UI accessible for a while
        System.out.println("Press Enter to exit and stop the SparkContext");
        new Scanner(System.in).nextLine();
        spark.stop();
    }
}

