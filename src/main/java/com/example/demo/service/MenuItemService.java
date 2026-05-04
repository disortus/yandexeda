package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.MenuItemCreateRequest;
import com.example.demo.dto.MenuItemUpdateRequest;
import com.example.demo.entity.MenuItem;
import com.example.demo.entity.MenuItemTag;
import com.example.demo.entity.Restaurant;
import com.example.demo.entity.Tag;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.filter.MenuItemFilter;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.MenuItemTagRepository;
import com.example.demo.repository.RestaurantRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.specification.MenuItemSpecification;

// пример

@Service
public class MenuItemService {

    private final MenuItemRepository repository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final MenuItemTagRepository menuItemTagRepository;

    public MenuItemService(
            MenuItemRepository repository,
            RestaurantRepository restaurantRepository,
            CategoryRepository categoryRepository,
            TagRepository tagRepository,
            MenuItemTagRepository menuItemTagRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.menuItemTagRepository = menuItemTagRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getAll(Long restaurantId, MenuItemFilter filter) {
        return repository.findAll(
                MenuItemSpecification.fromFilter(restaurantId, filter)
        );
    }

    @Transactional(readOnly = true)
    public MenuItem getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item with id " + id + " not found"));
    }

    @Transactional
    public MenuItem create(Long restaurantId, MenuItemCreateRequest request) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"));

        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        if (request.getCategoryId() != null) {
            item.setCategory(categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category with id " + request.getCategoryId() + " not found")));
        }
        item.setName(request.getName().trim());
        item.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        item.setPrice(request.getPrice());
        item.setAvailable(true);
        return repository.save(item);
    }

    @Transactional
    public MenuItem update(Long dishId, MenuItemUpdateRequest request) {
        MenuItem item = getById(dishId);

        if (request.getName() != null) {
            item.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription().trim());
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }
        if (request.getIsAvailable() != null) {
            item.setAvailable(request.getIsAvailable());
        }
        return repository.save(item);
    }

    @Transactional
    public void delete(Long dishId) {
        if (!repository.existsById(dishId)) {
            throw new ResourceNotFoundException("Menu item with id " + dishId + " not found");
        }
        repository.deleteById(dishId);
    }

    @Transactional
    public void addTag(Long dishId, Long tagId) {
        MenuItem item = getById(dishId);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id " + tagId + " not found"));

        if (menuItemTagRepository.existsByMenuItemAndTag(item, tag)) {
            throw new BusinessValidationException("Tag already attached to menu item");
        }

        menuItemTagRepository.save(new MenuItemTag(item, tag));
    }

    @Transactional
    public void removeTag(Long dishId, Long tagId) {
        MenuItem item = getById(dishId);
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag with id " + tagId + " not found"));
        menuItemTagRepository.deleteByMenuItemAndTag(item, tag);
    }

    @Transactional(readOnly = true)
    public List<Tag> getTags(Long dishId) {
        MenuItem item = getById(dishId);
        return menuItemTagRepository.findByMenuItem(item).stream()
                .map(MenuItemTag::getTag)
                .toList();
    }
}