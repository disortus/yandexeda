package com.example.demo.repository.example;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.example.SimpleUserEntityExample;

public interface SimpleUserRepositoryExample {

    List<SimpleUserEntityExample> findAll();

    Optional<SimpleUserEntityExample> findById(Long id);

    SimpleUserEntityExample save(SimpleUserEntityExample user);
}
