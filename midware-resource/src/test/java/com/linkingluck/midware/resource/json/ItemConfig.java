package com.linkingluck.midware.resource.json;

import com.linkingluck.midware.resource.anno.Resource;
import com.linkingluck.midware.resource.anno.ResourceId;

@Resource(suffix = "json")
public class ItemConfig {

    @ResourceId
    private int id;

    private String name;

    private int quality;

    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
