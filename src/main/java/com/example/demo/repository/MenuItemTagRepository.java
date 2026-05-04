package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.MenuItem;
import com.example.demo.entity.MenuItemTag;
import com.example.demo.entity.MenuItemTagId;
import com.example.demo.entity.Tag;

@Repository
public interface MenuItemTagRepository extends JpaRepository<MenuItemTag, MenuItemTagId> {
    List<MenuItemTag> findByMenuItem(MenuItem menuItem);

    List<MenuItemTag> findByTag(Tag tag);

    boolean existsByMenuItemAndTag(MenuItem menuItem, Tag tag);

    void deleteByMenuItemAndTag(MenuItem menuItem, Tag tag);
}
