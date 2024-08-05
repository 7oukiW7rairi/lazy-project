package org.lazy.web;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;
import java.util.*;

@JacksonXmlRootElement(localName = "dummy")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class DummyTestClass implements Serializable {

    private int id;
    private String name;
    private boolean valid;
    private Date dateTime;

    public DummyTestClass() {
    }

    public DummyTestClass(Integer id, String name, boolean valid, Date dateTime) {
        this.id = id;
        this.name = name;
        this.valid = valid;
        this.dateTime = dateTime;
    }

    @JsonProperty
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        valid = valid;
    }

    @JsonProperty
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="GMT")
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
