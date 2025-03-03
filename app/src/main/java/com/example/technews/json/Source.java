package com.example.technews.json;

import com.google.gson.annotations.SerializedName;

public class Source {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public Source(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Source() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
