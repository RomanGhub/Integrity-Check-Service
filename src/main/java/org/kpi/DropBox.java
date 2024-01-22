package org.kpi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

//@RestController  //restcontroller does not work with th
@Controller
@RequestMapping("/api/files")
public class DropBox {

//    @Autowired
//    private JarLoader jarLoader;

    @Autowired
    private IntegrityExecutor integrityExecutor;

    @Autowired
    private FileService fileService;


    @GetMapping("/drop")
    public String index(Model model) {
        model.addAttribute("name", "John");
        return "indexfancy";
    }

    @GetMapping("/listOfData")
    @ResponseBody
    public List<String> getAllDataArrayNames() {
        return fileService.getAllDataArrays();
    }

    @GetMapping("/listOfFiles")
    @ResponseBody
    public List<String> getAllFileNames() {
        return fileService.getAllFileNames();
    }

    @PostMapping("/run")
    @ResponseBody
    public ResponseEntity<String> runAlgorithm(@RequestParam String filename, @RequestParam("dataArray") String dataArray,
                                               @RequestParam("precalculatedHash") String precalculatedHash) throws Exception {
        String rootHash = integrityExecutor.executeIntegrityCheck(filename, dataArray, precalculatedHash, "");
        return new ResponseEntity<>(rootHash, HttpStatus.OK);
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam Boolean isDataArray) {
        if (file.isEmpty()) {
            System.out.println("No file uploaded");
            return new ResponseEntity<>("No file uploaded", HttpStatus.BAD_REQUEST);
        }

//        // Check if the uploaded file is a JAR file
//        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".jar") || !isDataArray) {  //.jar  and only one extension can be passed
//            System.out.println("Only JAR files are allowed");
//            return new ResponseEntity<>("Only JAR files are allowed", HttpStatus.BAD_REQUEST);
//        } //maybe this one isn't needed

        // Define the directory where you want to save the JAR file
        String uploadDir = "./uploads";  // later change to set it in properties
        if (isDataArray) {
            uploadDir = "./uploadedDataArrays";  // later change to set it in properties
        }
        Path uploadPath = Path.of(uploadDir);

        // You can save the file or process it as needed
        try {
            // Create the upload directory if it doesn't exist
            Files.createDirectories(uploadPath);

            // Normalize the file name
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            // Copy the JAR file to the upload directory
            Path targetPath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File uploaded successfully");

            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error uploading file: " + e.getMessage());
            return new ResponseEntity<>("Error uploading file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}