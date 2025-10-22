/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.dto;

import java.io.Serializable;

/**
 *
 * @author chris
 */
public class Block implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private Integer order;
    private String content;
    private Person person;
    private Scene scene;
    private Boolean isBookmarked;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Boolean getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(Boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
}
