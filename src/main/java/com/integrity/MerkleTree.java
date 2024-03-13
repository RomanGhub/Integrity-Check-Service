package com.integrity;

import org.apache.spark.api.java.JavaPairRDD;
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

import static com.integrity.IntegrityExecutor.fileData;

@Component
public class MerkleTree {

    private static Boolean firstFlag = true;

    @Autowired
    private SparkSessionRunner sessionRunner;

    @Autowired
    private ResultMonitoringService monitoringService;

    @Autowired
    private JarLoader jarLoader;


//    @Autowired
    private MethodInvoker methodInvoker; // = new MethodInvoker();

    public String merkle(LoadableClass integrityClass) throws Exception { //Object integrityClass

//        HashSet<Long> timeset = new HashSet<>();

        // INVOKER
/*        MethodInvoker methodInvoker = new MethodInvoker(integrityClass);
//        this.methodInvoker = methodInvoker;
        methodInvoker.setCurrentTargetObject(integrityClass);*/


        // Measure time before verification
        System.out.println("THIS IS MULTITHREADED VERSION");
        long startTime = System.currentTimeMillis();

        firstFlag = true;

        // Assuming fileData is your RDD with filename and PortableDataStream
        JavaPairRDD<String, String> hashedData = fileData.mapToPair(tuple -> {  // mapToPair is parallelized
            String filename = tuple._1();
            PortableDataStream dataStream = tuple._2();

            // Step 1: Parallel Hashing
            String hash = hashFunction(dataStream.open(), integrityClass, methodInvoker);

            //Flag switch
            firstFlag = false;

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

        // Step 4: Verify Integrity
        String expectedRootHash = calculateExpectedRootHash(fileData.collect(), integrityClass, methodInvoker);
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

    private static String hashFunction(InputStream inputStream, LoadableClass integrityClass, MethodInvoker methodInvoker) //Object integrityClass
            throws Exception, NoSuchMethodException, InvocationTargetException, IllegalAccessException { //throws NoSuchAlgorithmException

        Method method = integrityClass.getClass().getMethod("check", InputStream.class);

        Object[] parameters = {inputStream};

        return integrityClass.check(inputStream);
//        return (String) methodInvoker.invokeMethod(method.getName(), firstFlag, parameters); // with INVOKER
//        return (String)method.invoke(integrityClass, parameters);  //reflection
//        return check(inputStream); // mock
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

    private String calculateExpectedRootHash(List<Tuple2<String, PortableDataStream>> fileData, LoadableClass integrityClass, MethodInvoker methodInvoker)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, Exception { //Object integrityClass
        // Iterate through the fileData and construct the Merkle tree
        // Return the root hash

        StringBuilder concatenatedData = new StringBuilder();
        for (Tuple2<String, PortableDataStream> tuple : fileData) {
            concatenatedData.append(hashFunction(tuple._2().open(), integrityClass, methodInvoker));
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

/*
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
*/

}

