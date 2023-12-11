package org.kpi;

import org.apache.hadoop.mapred.lib.CombineFileSplit;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.input.PortableDataStream;
import org.apache.spark.rdd.RDD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import scala.Tuple2;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class IntegrityExecutor {

    @Autowired
    private MerkleTree merkleTree;

    @Autowired
    private SparkSessionRunner spark;

    @Autowired
    private JarLoader jarLoader;

    @Value("${algorithm.folder}")
    private String jarPath;

    @Value("${data.array.folder}")
    private String dataArrayPath;

    @Value("${min.partitions}")
    int minPartitions;

    public static JavaRDD<Tuple2<String, PortableDataStream>> fileData;
    public String executeIntegrityCheck(String fileName, String dataArray, String precalculatedHash, String customClassName) throws Exception {

//        String jarPath = "C:\\IdeaProjects\\IntegrityInstruments\\uploads\\"; //TODO put it in properties
        updateSparkJars(jarPath, fileName);
        //Load class into app
        Object integrityClass = jarLoader.loadJar(fileName, ""); //TODO


        // Specify the path to data
//        String dataArrayPath = "C:\\IdeaProjects\\IntegrityInstruments\\uploadedDataArrays\\"; //"./uploadedDataArrays/" + dataArray; // TODO this should be in properties

        splitFile(dataArrayPath + dataArray, 12, dataArrayPath + dataArray + "folder"); // + dataArray + "folder"




/*        // THIS ONE WORKS but I need pairs
        RDD<String> fileContents = spark.sparkSession.sparkContext().textFile(dataArrayPath, minPartitions);
        System.out.println(fileContents.getNumPartitions());*/

//        // ONLY 1 partition but in theory it can work
/*
        RDD<Tuple2<String, String>> fileContents = spark.sparkSession.sparkContext().
                wholeTextFiles(dataArrayPath + dataArray + "folder\\", minPartitions);
        System.out.println(fileContents.getNumPartitions());
*/


    //// another way, not exactly what i wanted
//        JavaRDD<String> fileData = spark.getSparkSession().sparkContext().parallelize(dataArrayPath, 8).toJavaRDD();


        /// one way, can only get 1 partition
        fileData = spark.getSparkSession().sparkContext().binaryFiles(dataArrayPath+dataArray, minPartitions).toJavaRDD();

        System.out.println(fileData);
        System.out.println("Number of partitions: " + fileData.getNumPartitions());

        fileData.repartition(10);
        System.out.println("Number of partitions: " + fileData.getNumPartitions());

//        HashmerkleTree.merkleSingleThread(integrityClass);
        String rootHash = merkleTree.merkle(integrityClass);

        if(rootHash.equals(precalculatedHash)){
        };

        return rootHash;
    }

    public static void splitFile(String originalFilePath, int numberOfChunks, String outputDirectory) {
        try (InputStream inputStream = new FileInputStream(originalFilePath)) {
            File originalFile = new File(originalFilePath);

            // Create the output directory if it doesn't exist
            createOutputDirectory(outputDirectory);

            long fileSize = originalFile.length();
            long chunkSize = fileSize / numberOfChunks;

            byte[] buffer = new byte[(int) chunkSize];

            for (int i = 0; i < numberOfChunks; i++) {
                String outputFilePath = outputDirectory + File.separator + originalFile.getName() + "_part" + (i + 1) + ".txt";
                try (OutputStream outputStream = new FileOutputStream(outputFilePath)) {
                    int bytesRead = inputStream.read(buffer);
                    if (bytesRead == -1) {
                        break; // End of file
                    }
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createOutputDirectory(String outputDirectory) {
        Path path = Paths.get(outputDirectory);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private  void updateSparkJars(String folderPath, String filename) {
        try {
            // List all JAR files in the specified folder
            Set<String> jarPathsInFolder = listJarsInFolder(folderPath);

            spark.sparkSession.sparkContext().addJar(folderPath + filename);

            SparkConf sparkConf2 = spark.sparkSession.sparkContext().getConf();
            System.out.println(sparkConf2);

            // Now, sparkSession will use the updated set of JAR paths
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<String> listJarsInFolder(String folderPath) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath), "*.jar")) {
            Set<String> jarPaths = new HashSet<>();
            for (Path entry : stream) {
                jarPaths.add(entry.toString());
            }
            return jarPaths;
        }
    }

/*
    //old code

    // Read the file into an RDD of (file path, byte array)
//        JavaRDD<Tuple2<String, PortableDataStream>> fileData = sparkSession.sparkContext().binaryFiles(dataArrayPath, minPartitions).toJavaRDD();
    //this one is working    JavaRDD<Tuple2<String, PortableDataStream>> fileData = spark.getSparkSession().sparkContext().binaryFiles(dataArrayPath, minPartitions).toJavaRDD();
//        // Read the TGZ archive as binary data  // I failed at this approach unfortunately
//        JavaRDD<byte[]> binaryDataRDD = sparkSession.sparkContext().binaryFiles(dataArrayPath, 1)
//                .map((Function<Tuple2<String, PortableDataStream>, byte[]>) tuple -> tuple._2().toArray(), Encoders.kryo(byte[].class) );
//        Dataset<String> dataset = sparkSession.read().textFile(logFile).cache(); // old variant with readme

    /////// Old code from JarLoader
    // Invoke a method (assuming there is a method called "someMethod" with no parameters)
//        Method method = loadedClass.getMethod("check");
//        method.invoke(instance);

*/

/*
    public static void main(String[] args) {

        String logFile = "README2.md";
//        String logFile = "home/romaniy/sparkFiles/README2.md";
//        String logFile = "C:\\IdeaProjects\\IntegrityInstruments\\README2.md";// Should be some file on your system
        SparkSession spark = null;
        try {
            spark = SparkSession.builder().appName("Main").config("spark.master", "local[*]").getOrCreate(); //local[*] ////localhost:7077 seems not working
            spark.sparkContext().setLogLevel("ERROR");
            System.out.println("Context created");


            Dataset<String> logData = spark.read().textFile(logFile).cache();

            long numAs = 4;//logData.filter("1-4" ).count();
            long numBs = logData.filter(col("value").rlike(".*[A-Za-z].*")).count();
//        long numAs = logData.filter((Function1<String, Object>) s -> s.contains("a")).count();
//        long numBs = logData.filter((Function1<String, Object>) s -> s.contains("b")).count();

            System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);


            if (spark.sparkContext().isStopped()) {
                System.out.println("stopped 2");
            }

//        spark.stop();

            if (spark.sparkContext().isStopped()) {
                System.out.println("stopped 3");
            }
        } catch (Exception e) {
            System.out.println("Error: Cannot call methods on a stopped SparkContext");
            e.printStackTrace();
        } finally {
            assert spark != null;
            // Add a delay or user input to keep the Spark UI accessible for a while
            System.out.println("Press Enter to exit and stop the SparkContext");
            new Scanner(System.in).nextLine();
            spark.stop();
        }
    }
*/

}