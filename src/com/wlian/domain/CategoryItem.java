package com.wlian.domain;

import java.util.ArrayList;
import java.util.List;

public class CategoryItem {
    private Category category;
    List<Category> categories = new ArrayList<Category>();

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
