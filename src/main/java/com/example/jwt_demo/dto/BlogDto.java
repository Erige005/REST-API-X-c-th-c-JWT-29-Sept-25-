package com.example.jwt_demo.dto;

public class BlogDto {
    private Long id;
    private String title;
    private String content;
    // getters/setters
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getTitle(){return title;}
    public void setTitle(String t){this.title=t;}
    public String getContent(){return content;}
    public void setContent(String c){this.content=c;}
}
