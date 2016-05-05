package org.nisum.builder;

import org.junit.Test;
import org.nisum.model.Author;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class AuthorBuilderTest {
    
    private static final String LASTNAME = "Perez";
    private static final String NAME = "Jose";

    @Test
    public void testWithName() {
        
        Author author = new AuthorBuilder().withName(NAME).withLastName(LASTNAME).build();
        assertEquals(NAME,author.getName());

    }

    @Test
    public void testWithLastName() {
        Author author = new AuthorBuilder().withName(NAME).withLastName(LASTNAME).build();
        assertEquals(LASTNAME,author.getLastName());
    }

    @Test
    public void testDone() {
        assertNull(new AuthorBuilder().done());
        assertNotNull(new AuthorBuilder(new BookBuilder()).done());
    }


}
