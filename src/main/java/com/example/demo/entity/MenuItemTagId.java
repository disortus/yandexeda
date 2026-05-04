        package com.example.demo.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class MenuItemTagId implements Serializable {
    @Column(name = "menu_item_id")
    private Long menuItemId;

    @Column(name = "tag_id")
    private Long tagId;

    public MenuItemTagId() {
    }

    public MenuItemTagId(Long menuItemId, Long tagId) {
        this.menuItemId = menuItemId;
        this.tagId = tagId;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuItemTagId that)) {
            return false;
        }
        return Objects.equals(menuItemId, that.menuItemId)
                && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuItemId, tagId);
    }
}