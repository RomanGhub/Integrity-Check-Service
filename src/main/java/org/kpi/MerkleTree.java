package org.kpi;

import com.google.common.hash.Hashing;
import org.apache.hadoop.util.Time;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.input.PortableDataStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.Tuple2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.kpi.IntegrityExecutor.fileData;

@Component
public class MerkleTree {

    @Autowired
    private SparkSessionRunner sessionRunner;

    @Autowired
    private ResultMonitoringService monitoringService;

    @Autowired
    private JarLoader jarLoader;

    private Object targetObject;
    private Map<String, Method> methodCache;


    // invoke
    public void MethodInvoker(Object targetObject) {
        this.targetObject = targetObject;
        this.methodCache = new ConcurrentHashMap<>();
    }

    public Object invokeMethod(String methodName, Object... args) throws Exception {
        // Check if the method is already cached
        Method method = methodCache.computeIfAbsent(methodName, this::getDeclaredMethod);

        // Invoke the method using reflection
        return method.invoke(targetObject, args);
    }

    private Method getDeclaredMethod(String methodName) {
        // Use reflection to get the declared method
        try {
            return targetObject.getClass().getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found: " + methodName, e);
        }
    }
    //

    public void merkleSingleThread(Object integrityClass)
            throws NoSuchAlgorithmException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        // Assuming fileData is your RDD with filename and PortableDataStream
        List<Tuple2<String, PortableDataStream>> fileDataList = fileData.collect();

        System.out.println("THIS IS SINGLE THREADED VERSION");
        // Measure time before verification
        long startTime = System.currentTimeMillis();

        // Step 1: Sequential Hashing
        List<Tuple2<String, String>> hashedDataList = new ArrayList<>();
        for (Tuple2<String, PortableDataStream> tuple : fileDataList) {
            String filename = tuple._1();
            PortableDataStream dataStream = tuple._2();
            String hash = hashFunction(dataStream.open(), integrityClass); //integrityClass
            hashedDataList.add(new Tuple2<>(filename, hash));
        }
        long afterHashingTime = System.currentTimeMillis(); //TODO
        System.out.println("After hashing time is " + (afterHashingTime - startTime));

        // Step 2: Construct Sequential Partial Merkle Trees
        List<Tuple2<String, String>> partialTreesList = new ArrayList<>();
        for (Tuple2<String, String> hashedTuple : hashedDataList) {
            String filename = hashedTuple._1();
            String hash = hashedTuple._2();
            // Your logic for constructing partial trees here
            partialTreesList.add(new Tuple2<>(filename, hash));
        }
        long afterConstructingTime = System.currentTimeMillis(); //TODO
        System.out.println("After constructing time is " + (afterConstructingTime- afterHashingTime));

        // Step 3: Combine Sequential Partial Trees
        String rootHash = "";
        for (Tuple2<String, String> partialTree : partialTreesList) {
            String hash = partialTree._2();
            // Your logic for combining partial trees here
            rootHash = combineHashes(rootHash, hash);
        }
        long afterCombiningPartialTreesTime = System.currentTimeMillis(); //TODO
        System.out.println("After combining partial trees time is " + (afterCombiningPartialTreesTime - afterConstructingTime));

        // Step 4: Verify Integrity (Sequential in this case)
        String expectedRootHash = calculateExpectedRootHash(fileDataList, integrityClass);
        boolean integrityCheckPassed = rootHash.equals(expectedRootHash);

        // Measure time after verification
        System.out.println("RootHash is: " + rootHash);
        System.out.println("expected RootHash is:" + expectedRootHash);
        System.out.println("Integrity Check of Passed: " + integrityCheckPassed);
        System.out.println("It took " + (System.currentTimeMillis() - startTime) + "ms to run");
    }

    public String merkle(Object integrityClass) throws NoSuchAlgorithmException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // Set up Spark
//        SparkContext sparkContext = sessionRunner.getSparkSession().sparkContext();

//        HashSet<Long> timeset = new HashSet<>();

        // Measure time before verification
        System.out.println("THIS IS MULTITHREADED VERSION");
        long startTime = System.currentTimeMillis();

        // Assuming fileData is your RDD with filename and PortableDataStream
        JavaPairRDD<String, String> hashedData = fileData.mapToPair(tuple -> {  // mapToPair is parallelized
            String filename = tuple._1();
            PortableDataStream dataStream = tuple._2();

            // Step 1: Parallel Hashing
            String hash = hashFunction(dataStream.open(), integrityClass);

//            timeset.add(System.currentTimeMillis());

            return new Tuple2<>(filename, hash);
        });

        long afterHashingTime = System.currentTimeMillis() - startTime; //TODO
        System.out.println("After hashing time is " + afterHashingTime );

        // Step 2: Construct Partial Merkle Trees
        JavaPairRDD<String, String> partialTrees = hashedData.reduceByKey((hash1, hash2) -> combineHashes(hash1, hash2));
        long afterConstructingTime = System.currentTimeMillis() - afterHashingTime; //TODO
        System.out.println("After constructing time is " + afterConstructingTime);

        // Measure memory usage before verification
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(); //TODO

        // Step 3: Combine Partial Trees
        String rootHash = partialTrees.values().reduce((hash1, hash2) -> combineHashes(hash1, hash2));
        long afterCombiningPartialTreesTime = System.currentTimeMillis() - afterConstructingTime; //TODO
        System.out.println("After combining partial trees time is " + afterCombiningPartialTreesTime);


//        SparkConf conf = sparkContext.getConf();
//        String numExecutors = conf.get("spark.executor.instances");
//        String executorMemory = conf.get("spark.executor.memory");
//        String executorCores = conf.get("spark.executor.cores");
//
//        System.out.println("Number of Executors: " + numExecutors);
//        System.out.println("Executor Memory: " + executorMemory);
//        System.out.println("Executor Cores: " + executorCores);


        // Step 4: Verify Integrity
        String expectedRootHash = calculateExpectedRootHash(fileData.collect(), integrityClass);
        boolean integrityCheckPassed = rootHash.equals(expectedRootHash);

        // Measure time usage after verification
        System.out.println("RootHash is: " + rootHash);
        System.out.println("expected RootHash is:" + expectedRootHash);
        System.out.println("Integrity Check of " + "Passed: " + integrityCheckPassed); // + fileName +
        System.out.println("It took " + (System.currentTimeMillis() - startTime) + "ms to run");

        // Measure memory usage after verification
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryUsage = memoryAfter - memoryBefore;

        System.out.println("Memory Usage : " + memoryUsage + " bytes"); //TODO

        return rootHash;
    }

    private static String hashFunction(InputStream inputStream, Object integrityClass)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException { //throws NoSuchAlgorithmException

        Method method = integrityClass.getClass().getMethod("check", InputStream.class);

        Object[] parameters = {inputStream};

//        return (String)method.invoke(integrityClass, parameters);
        return check(inputStream);
    }


    private static String combineHashes(String hash1, String hash2) throws NoSuchAlgorithmException {
        // Concatenate the two hashes
        String concatenatedHashes = hash1 + hash2;

        // Use a cryptographic hash function (SHA-256) to hash the concatenated string
        MessageDigest digest = MessageDigest.getInstance("MD5"); //SHA-256
        byte[] combinedHashBytes = digest.digest(concatenatedHashes.getBytes());

        // Convert the byte array to a hexadecimal string
        return bytesToHex(combinedHashBytes);
    }
/*    // Concatenate or use a cryptographic hash combination function
    private static String combineHashes(String hash1, String hash2) {
        return hash1 + hash2;
    }*/

    // Replace this with your logic to calculate the expected root hash
    private String calculateExpectedRootHash(List<Tuple2<String, PortableDataStream>> fileData, Object integrityClass)
            throws NoSuchAlgorithmException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // Iterate through the fileData and construct the Merkle tree
        // Return the root hash

        StringBuilder concatenatedData = new StringBuilder();
        for (Tuple2<String, PortableDataStream> tuple : fileData) {
            concatenatedData.append(hashFunction(tuple._2().open(), integrityClass));
        }
        return concatenatedData.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

//    public static String check(InputStream inputStream) { //byte[]
//        System.out.println("THIS IS TEST METHOD INVOCATION");
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5"); //SHA-256 //MD5
//
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                md.update(buffer, 0, bytesRead);
//            }
//            byte[] digest = md.digest();
//            return bytesToHex(digest);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//
//        }
//    }



    public static String check(InputStream inputStream) {
        System.out.println("THIS IS TEST METHOD INVOCATION");
        try {
            com.google.common.hash.HashFunction murmur3 = Hashing.murmur3_32();
            com.google.common.hash.Hasher hasher = murmur3.newHasher();

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                hasher.putBytes(buffer, 0, bytesRead);
            }
            int hashValue = hasher.hash().asInt();
            return Integer.toHexString(hashValue);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
