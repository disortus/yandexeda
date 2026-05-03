@Entity
@Table(name="menu_item_tags")
public class MenuItemTag {
    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public MenuItemTag() {
    }
    
    public MenuItemTag(MenuItem menuItem, Tag tag) {
        this.menuItem = menuItem;
        this.tag = tag;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}