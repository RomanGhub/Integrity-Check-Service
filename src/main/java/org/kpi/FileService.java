package org.kpi;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileService {

    public List<String> getAllFileNames() {
        // Provide the path to your folder
        String folderPath = "./uploads"; //set it from properties in future

        File folder = new File(folderPath);
        try {
            if (folder.exists() && folder.isDirectory()) {
                return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                        .filter(File::isFile)
                        .map(File::getName)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Handle the case when the folder doesn't exist or is not a directory
    }

    public List<String> getAllDataArrays() {
        // Provide the path to your folder
        String folderPath = "./uploadedDataArrays"; //set it from properties in future

        File folder = new File(folderPath);
        try {
            if (folder.exists() && folder.isDirectory()) {
                return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                        .filter(File::isFile)
                        .map(File::getName)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Handle the case when the folder doesn't exist or is not a directory
    }
}
