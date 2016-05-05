package org.nisum.controller;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nisum.dao.BooksDao;
import org.nisum.model.Author;
import org.nisum.model.Book;
import org.nisum.service.BookService;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class BooksControllerTest {
    
    List<Book> myBooks = new ArrayList<Book>();
    List<Book> booksByAuthor = new ArrayList<Book>();
    
    Book book1,book2,book3;

    @Mock
    BookService bookService;
    
    @Mock
    BooksDao booksDao;
    
    @InjectMocks
    BooksController bookController;
    
    @Before
    public void setUp() {
        
        book1= new Book("1", "Amor en tiempos de colera", "Editorial 1", new Author(
                "Gabriel", "Marquez"));
        book2=new Book("2", "Muerte Anunciada", "Editorial 2", new Author(
                       "Gabriel", "Marquez"));
        book3= new Book("3", "Libro de la Selva", "Editorial 3", new Author(
                       "Pedro", "Marquez"));
        myBooks.add(book1);
        myBooks.add(book2);
        myBooks.add(book3);
        
        booksByAuthor.add(book1);
        booksByAuthor.add(book2);
          
        when(booksDao.exist("1")).thenReturn(true);
        when(booksDao.exist("2")).thenReturn(true);
        when(booksDao.exist("3")).thenReturn(true);
        
        when(booksDao.getBook("1")).thenReturn(book1);
        when(booksDao.getBook("2")).thenReturn(book2);
        when(booksDao.getBook("3")).thenReturn(book3);
        
        when(booksDao.deleteBook("1")).thenReturn(book1);
        when(booksDao.deleteBook("2")).thenReturn(book2);
        when(booksDao.deleteBook("3")).thenReturn(book3);
  
        when(booksDao.getAllBooks()).thenReturn(myBooks);
        when(booksDao.getListByAuthor("Gabriel")).thenReturn(booksByAuthor);

        RestAssuredMockMvc.standaloneSetup(bookController);

    }

    @Test
    public void testGetBook() {
        when(bookService.getBook("1")).thenReturn(book1);

        given().
        when().
            get("/book/get/{idBook}",1).
        then().
            assertThat().
            body("id",equalTo("1")).
            body("name",equalTo("Amor en tiempos de colera")).
            body("editorial",equalTo("Editorial 1")).
            body("author.name",equalTo("Gabriel")).
            body("author.lastName",equalTo("Marquez")).
            statusCode(SC_OK);
    }

    @Test
    public void testGetBookListEmpty() {
        List<Book> emptyBooks = new ArrayList<>();
        when(booksDao.getAllBooks()).thenReturn(emptyBooks);

        given().
        when().
            get("/book/list").
        then().
            statusCode(SC_OK);
    }

    @Test
    public void testNotFoundBook(){
        doThrow(new BookService().new ReadErrorException("")).when(bookService).getBook(anyString());

        given().
        when().
            get("/book/get/{idBook}",6).
        then().
            statusCode(SC_NOT_FOUND);  
    }

    @Test
    public void testCreateBook(){
        given().
            contentType(ContentType.JSON).
            body(new Book("7","El Lobito","Editorial 7",new Author("Carlos","Fuentes"))).
        when().
            post("/book/create").
        then()
            .statusCode(SC_CREATED).
            assertThat().
            body("id",equalTo("7")).
            body("name",equalTo("El Lobito")).
            body("editorial",equalTo("Editorial 7")).
            body("author.name",equalTo("Carlos")).
            body("author.lastName", equalTo("Fuentes"));
        
    }

    @Test
    public void testFailCreateBook(){
        doThrow(bookService.new CreationErrorException("")).when(bookService).createBook(any(Book.class));

        given().
            contentType(JSON).
            body(new Book("1","100 años","Alpahuara", new Author("Gabriel","García Márquez"))).
        when().
            post("/book/create").
        then()
            .statusCode(SC_NOT_ACCEPTABLE); 
    }

    @Test
    public void testDeleteBook(){
        when(bookService.deleteBook("3")).thenReturn(book3);

        given().
        when().
            delete("/book/delete/{idBook}",3).
        then().
            body("id", equalTo("3")).
            body("name", equalTo("Libro de la Selva")).
            body("editorial", equalTo("Editorial 3")).
            body("author.name", equalTo("Pedro")).
            body("author.lastName", equalTo("Marquez"))
            .statusCode(SC_OK);
    }

    @Test
    public void testUpdateBook(){
        given().
            contentType(ContentType.JSON).
            body(new Book("1","Colmillo Blanco","Alpahuara", new Author("Carlos","Garcia"))).
        when().
            put("/book/update").
        then().
            body("id", equalTo("1")).
            body("name", equalTo("Colmillo Blanco")).
            body("editorial", equalTo("Alpahuara")).
            body("author.name", equalTo("Carlos")).
            body("author.lastName", equalTo("Garcia")).
            statusCode(SC_OK); 
    }

    @Test
    public void testFailUpdateBook() {
        doThrow(new BookService().new UpdateErrorException("")).when(bookService).updateBook(any(Book.class));

        given().
            contentType(ContentType.JSON).
            body(new Book("10","Doce Cuentos Peregrinos","Alpahuara", new Author("Carlos","García Márquez"))).
        when().
            put("/book/update").
        then()
            .statusCode(SC_NOT_FOUND); 
    }

    @Test
    public void testFailDeleteBook() {
        when(bookService.deleteBook("8")).thenThrow(new BookService().new DeleteErrorException(anyString()));

        given().
        when().
            delete("/book/delete/{idBook}",8).
        then().
            statusCode(SC_NOT_FOUND);
    }

    @Test
    public void testGetAllBooks(){
        when(bookService.getAllBooks()).thenReturn(myBooks);

        given().
            contentType(ContentType.JSON).
        when().
            get("/book/list").
        then().
            body("id[0]", equalTo("1")).
            body("name[0]",equalTo("Amor en tiempos de colera")).
            body("editorial[0]",equalTo("Editorial 1")).
            body("author[0].name",equalTo("Gabriel")).
            body("author[0].lastName",equalTo("Marquez"))
            .statusCode(SC_OK);
    }

    @Test
    public void testGetListByAuthor(){
        when(bookService.getListByAuthor("Gabriel")).thenReturn(booksByAuthor);

        given().
            contentType(ContentType.JSON).
        when().
            get("/book/listByAuthor/{authorName}","Gabriel").
        then().
            body("id[0]", equalTo("1")).
            body("name[0]",equalTo("Amor en tiempos de colera")).
            body("editorial[0]",equalTo("Editorial 1")).
            body("author[0].name",equalTo("Gabriel")).
            body("author[0].lastName",equalTo("Marquez"))
            .statusCode(SC_OK);
    }
}