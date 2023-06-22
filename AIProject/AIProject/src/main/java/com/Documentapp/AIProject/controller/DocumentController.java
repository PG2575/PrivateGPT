package com.Documentapp.AIProject.controller;


import com.Documentapp.AIProject.Query.QueryRequest;
import com.Documentapp.AIProject.model.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/PrivateGPT/Documents")
public class DocumentController {

    private static final String WORKING_DIR = "D:\\Fiverr Projects\\AIProject\\privateGPT-main\\privateGPT-main\\";


    private static final String WORKING_DIR1 = "D:\\Fiverr Projects\\AIProject\\privateGPT-main\\privateGPT-main\\source_documents\\";

    private List<Document> documents = new ArrayList<>();

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String fileId = UUID.randomUUID().toString();
            String fileName = file.getOriginalFilename();
            String filePath = WORKING_DIR1 + "_" + fileName;
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(filePath));

            Document document = new Document(fileId, fileName, filePath);
            documents.add(document);

            return ResponseEntity.ok(fileId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the document.");
        }
    }



        @PostMapping("/query")
        public ResponseEntity<String> runQuery(@RequestBody QueryRequest queryRequest) {
            try {

                ProcessBuilder pb = new ProcessBuilder("python", "privateGPT.py");
                pb.directory(new File(WORKING_DIR));
                Process process = pb.start();

                OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                // Send query to the script
                writer.write(queryRequest.getQuery());
                writer.flush();
                writer.close();

                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                    responseBuilder.append("\n");
                }
                reader.close();

                String response = responseBuilder.toString();
                System.out.println(response);

                return ResponseEntity.ok(response);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process the query.");
            }
        }
    }





