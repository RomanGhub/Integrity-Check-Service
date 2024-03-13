//package org.kpi;
//
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.input.PortableDataStream;
//import scala.Tuple2;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//
//import java.io.InputStream;
//import java.util.List;
//
//import static com.integrity.IntegrityExecutor.fileData;
//
//public class MerkleTreeTest {
//
//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        // Set up Spark
//        JavaSparkContext sparkContext = new JavaSparkContext("local", "MerkleTreeExample");
//
//        // Assuming fileData is your RDD with filename and PortableDataStream
//        JavaPairRDD<String, String> hashedData = fileData.mapToPair(tuple -> {
//            String filename = tuple._1();
//            PortableDataStream dataStream = tuple._2();
//
//            // Step 1: Parallel Hashing
//            String hash = hashFunction(dataStream.open());
//
//            return new Tuple2<>(filename, hash);
//        });
//
//        // Step 2: Construct Partial Merkle Trees
//        JavaPairRDD<String, String> partialTrees = hashedData.reduceByKey((hash1, hash2) -> combineHashes(hash1, hash2));
//
//        // Step 3: Combine Partial Trees
//        String rootHash = partialTrees.values().reduce((hash1, hash2) -> combineHashes(hash1, hash2));
//
//        // Step 4: Verify Integrity
//        String expectedRootHash = calculateExpectedRootHash(fileData.collect());
//        boolean integrityCheckPassed = rootHash.equals(expectedRootHash);
//
//        System.out.println("Integrity Check Passed: " + integrityCheckPassed);
//
//        // Stop Spark context
//        sparkContext.stop();
//    }
//
//    // Replace this with your actual hash function implementation
//    private static String hashFunction(InputStream inputStream) throws NoSuchAlgorithmException {
//        // Implement your hash function logic using inputStream
//        // This is just a placeholder
////        return Utils.md5Hash(Utils.toByteArray(inputStream));
//
//
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
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
//        }
//    }
//
//
//    // Replace this with your actual hash combination logic
//    private static String combineHashes(String hash1, String hash2) {
//        // Concatenate or use a cryptographic hash combination function
//        // This is just a placeholder
////        return Utils.md5Hash(hash1 + hash2);
//
//        return hash1 + hash2;
//    }
//
//    // Replace this with your logic to calculate the expected root hash
//    private static String calculateExpectedRootHash(List<Tuple2<String, PortableDataStream>> fileData) throws NoSuchAlgorithmException {
//        // Iterate through the fileData and construct the Merkle tree
//        // Return the root hash
//        // This is just a placeholder
////        StringBuilder concatenatedData = new StringBuilder();
////        for (Tuple2<String, PortableDataStream> tuple : fileData) {
////            concatenatedData.append(hashFunction(tuple._2().open()));
////        }
////        return Utils.md5Hash(concatenatedData.toString());
//
//        ///
//
//        StringBuilder concatenatedData = new StringBuilder();
//        for (Tuple2<String, PortableDataStream> tuple : fileData) {
//            concatenatedData.append(hashFunction(tuple._2().open()));
//        }
//        return concatenatedData.toString();
//    }
//
//    private static String bytesToHex(byte[] bytes) {
//        StringBuilder result = new StringBuilder();
//        for (byte b : bytes) {
//            result.append(String.format("%02X", b));
//        }
//        return result.toString();
//    }
//}
//
