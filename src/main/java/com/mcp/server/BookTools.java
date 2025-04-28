package com.mcp.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookTools {
    
    private final List<Books> listOfBooks = new ArrayList<>();
    
    // constructor
    public BookTools() {
        // init the array
        var bookOne = new Books("Star Wars: Dark Force Rising", 
                "https://www.goodreads.com/book/show/216442.Star_Wars", 1992);
        var bookTwo = new Books("Star Wars: The Last Command", 
                "https://www.goodreads.com/book/show/216422.Star_Wars", 1993);
        var bookThree = new Books("Star Wars: Dark Lord - The Rise of Darth Vader", 
                "https://www.goodreads.com/book/show/359848.Star_Wars", 2005);
        var bookFour = new Books("Star Wars: Lost Tribe of the Sith - The Collected Stories",
             "https://www.goodreads.com/book/show/13023324-star-wars", 2012);
        var bookFive = new Books("Heir to the Empire", "https://www.goodreads.com/book/show/216443.Heir_to_the_Empire", 1991);
        var bookSix = new Books("Path of Destruction", "https://www.goodreads.com/book/show/35430.Path_of_Destruction", 2006);
        var bookSeven = new Books("Star Wars: Labyrinth of Evil", "https://www.goodreads.com/book/show/14978.Star_Wars", 2005);
        var bookEight = new Books("Star Wars: Outbound Flight", "https://www.goodreads.com/book/show/192523.Star_Wars", 2006);
        
        this.listOfBooks.addAll(List.of(
                bookOne, 
                bookTwo,
                bookThree,
                bookFour,
                bookFive,
                bookSix,
                bookSeven,
                bookEight
        ));

    }

    public List<Books> getBooks() {
        return this.listOfBooks; 
    }

    public List<Books> getBooksByYear(int year){
        return this.listOfBooks.stream()
            .filter(p -> p.year() == year).toList();
    }

    public List<Books> getBooksByYearRange(int fromYear, int toYear) {
        return this.listOfBooks.stream()
            .filter(p -> p.year() >= fromYear && p.year() <= toYear)
            .toList();
    }

    public List<Map<String, Object>> getBooksasMapList() {
        return this.listOfBooks.stream()
            .map( p  -> {
                Map<String, Object> map = new HashMap<>();
                map.put("title", p.title());
                map.put("url", p.url());
                map.put("year", p.year());
                return map;
            })
            .collect(Collectors.toList());
    } 
    
}
