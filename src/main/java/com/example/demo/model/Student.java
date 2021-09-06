package com.example.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private double rollNumber;
    private String remarks;

    public Student(){

    }

    public Student(String name, double rollNumber, String remarks) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.remarks = remarks;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(double rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}