package com.linkedin.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LibraryServiceTests {

  @InjectMocks
  private LibraryService underTest;

  @Mock
  private LibraryRepository libraryRepoMock;

  @Captor
  private ArgumentCaptor<String> titleCaptor;

  @Captor
  private ArgumentCaptor<Integer> countCaptor;

  @Test
  public void testGetMembers() {

    assertTrue(underTest.getMembers().isEmpty());
  }

  @Test
  public void testRegisterMember() {

    underTest.registerMember("Example ID 1", "Example Name 1");

    assertTrue(underTest.getMembers().size() == 1);

    underTest.registerMember("Example ID 2", "Example Name 2");

    assertTrue(underTest.getMembers().size() == 2);
  }

  @Test
  public void testAddBook() {

    underTest.addBook("Contact", 1);

    verify(libraryRepoMock).addBook(eq("Contact"), eq(1));

    underTest.addBook("Starship Troopers", 3);

    verify(libraryRepoMock, times(2))
        .addBook(titleCaptor.capture(), countCaptor.capture());

    System.out.println(titleCaptor.getAllValues().get(0));
    System.out.println(countCaptor.getAllValues().get(0));
    System.out.println(titleCaptor.getAllValues().get(1));
    System.out.println(countCaptor.getAllValues().get(1));

    assertEquals(titleCaptor.getValue(), "Starship Troopers");
    assertEquals(countCaptor.getValue(), 3);
  }

  @Test
  public void testLendBook() {

    when(libraryRepoMock.lendBook("The Hot Zone")).thenReturn(true);
    when(libraryRepoMock.lendBook("The Twilight Zone")).thenReturn(false);

    underTest.addBook("Jurassic Park", 3);
    underTest.addBook("The Hot Zone", 5);

    underTest.registerMember("ID 000-000-001", "Arthur Dent");
    underTest.registerMember("ID 000-000-002", "Zaphod Beeblebrox");

    verify(libraryRepoMock, never()).lendBook(any());

    assertTrue(underTest.lendBook("The Hot Zone", "ID 000-000-002"));

    verify(libraryRepoMock, times(1)).lendBook("The Hot Zone");

    assertFalse(underTest.lendBook("The Twilight Zone", "ID 000-000-002"));

    verify(libraryRepoMock, times(1)).lendBook("The Twilight Zone");

    assertFalse(underTest.lendBook("Jurassic Park", "ID XXX-XXX-XXX"));

    verify(libraryRepoMock, never()).lendBook("Jurassic Park");
  }
}
