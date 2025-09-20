package com.smart.rides.controller;

import com.smart.rides.entity.TestEntity;
import com.smart.rides.repository.TestEntityRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestEntityController {

    private final TestEntityRepository repository;

    public TestEntityController(TestEntityRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TestEntity> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public TestEntity create(@RequestBody TestEntity entity) {
        return repository.save(entity);
    }
}
