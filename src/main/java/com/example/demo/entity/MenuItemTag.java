package com.example.demo.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;


@Entity
@Table(name="menu_item_tags")
public class MenuItemTag {
    @EmbeddedId
    private MenuItemTagId id = new MenuItemTagId();

    @MapsId("menuItemId")
    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @MapsId("tagId")
    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public MenuItemTag() {
    }
    
    public MenuItemTag(MenuItem menuItem, Tag tag) {
        this.menuItem = menuItem;
        this.tag = tag;
        if (menuItem != null && tag != null) {
            this.id = new MenuItemTagId(menuItem.getId(), tag.getId());
        }
    }

    public MenuItemTagId getId() {
        return id;
    }

    public void setId(MenuItemTagId id) {
        this.id = id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        if (this.id == null) {
            this.id = new MenuItemTagId();
        }
        this.id.setMenuItemId(menuItem != null ? menuItem.getId() : null);
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        if (this.id == null) {
            this.id = new MenuItemTagId();
        }
        this.id.setTagId(tag != null ? tag.getId() : null);
    }
}