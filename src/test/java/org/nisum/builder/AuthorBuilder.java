package org.nisum.builder;

import org.nisum.model.Author;

public class AuthorBuilder {
    
    private BookBuilder bookBuilder;
    private String name;
    private String lastName;
    

    public AuthorBuilder(BookBuilder bookBuilder) {
        this.bookBuilder = bookBuilder;
    }

    public AuthorBuilder() {
    }

    public AuthorBuilder withName(String name){
        this.name=name;
        return this;
    }
    
    public AuthorBuilder withLastName(String lastName){
        this.lastName=lastName;
        return this;
    }
    
    public Author build(){
        return  new Author(name,lastName);
    }
    
    public BookBuilder done(){
        return bookBuilder;
    }

}
