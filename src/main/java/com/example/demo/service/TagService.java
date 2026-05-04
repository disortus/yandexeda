package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.TagCreateRequest;
import com.example.demo.dto.TagResponse;
import com.example.demo.entity.Tag;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.filter.TagFilter;
import com.example.demo.repository.TagRepository;
import com.example.demo.specification.TagSpecification;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getAll(TagFilter filter) {
        return tagRepository.findAll(TagSpecification.fromFilter(filter)).stream()
                .map(t -> new TagResponse(t.getId(), t.getName()))
                .toList();
    }

    @Transactional
    public TagResponse create(TagCreateRequest request) {
        String name = request.getName().trim();
        if (tagRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessValidationException("Tag with name '" + name + "' already exists");
        }

        Tag tag = new Tag();
        tag.setName(name);
        Tag saved = tagRepository.save(tag);
        return new TagResponse(saved.getId(), saved.getName());
    }
}