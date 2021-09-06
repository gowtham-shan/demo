package com.example.demo.controller;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<List<Student>> listAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                 @RequestParam(defaultValue = "id") String sortBy) {
        List<Student> list = studentService.getAllStudents(pageNo, pageSize, sortBy);

        return new ResponseEntity<List<Student>>(list, new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * Excel Import using Apache POI Library
     * @param file
     * @return
     */

    @PostMapping("/v1/import")
    public String uploadStudents(@RequestParam("file") MultipartFile file) {
        String response = "success";

        if (file.isEmpty()) {
            response = "Please choose file";
        } else {
            try {
                List<Student> studentList = new ArrayList<>();
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet worksheet = workbook.getSheetAt(0);
                for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
                    if (index > 0) {
                        XSSFRow row = worksheet.getRow(index);
                        Student new_student = new Student(row.getCell(0).getStringCellValue(), index, row.getCell(1).getStringCellValue());
                        studentList.add(new_student);
                    }
                }
                if(!studentList.isEmpty()){
                    studentRepository.saveAll(studentList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    /**
     * Import Excel Data using excel-streaming-reader
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/v2/import")
    public String uploadStudentsV2(@RequestParam("file") MultipartFile file) throws IOException {
        String response = "success";

        if (file.isEmpty()) {
            response = "Please choose file";
        } else {
            long start = System.currentTimeMillis();
            File inputFile = studentService.convertMultiPartToFile(file);
            try(
                    InputStream is = new FileInputStream(inputFile);
                    Workbook wb = StreamingReader.builder()
                            .sstCacheSize(1000)
                            .open(is);
            ) {
                Sheet sheet = wb.getSheetAt(0);
                List<Student> studentList = new ArrayList<>();
                long count = 0;
                for(Row row : sheet) {
                    count++;
                    if (count>0 && count <=1000000) {
                        Student new_student = new Student(row.getCell(0).getStringCellValue(), count, row.getCell(1).getStringCellValue());
                        studentList.add(new_student);
                        System.out.println(count);
                    }
                }
                System.out.println("Read "+count+" rows in "+(System.currentTimeMillis() - start)+"ms");
                start = System.currentTimeMillis();
                if(!studentList.isEmpty()){
                    studentRepository.saveAll(studentList);
                }
                System.out.println("written "+count+" rows in "+(System.currentTimeMillis() - start)+"ms");
            }
        }
        return response;
    }
}