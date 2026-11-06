package com.mikhail.labs.service;

import com.mikhail.labs.exception.AlbumNotFoundException;
import com.mikhail.labs.model.Album;
import com.mikhail.labs.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-тесты сервисного слоя: репозиторий замокан, база данных не используется.
 */
@ExtendWith(MockitoExtension.class)
class AlbumServiceImplTest {

    private static final Album EXISTING_ALBUM = new Album(1, "The Dark Side of the Moon", 1973);
    private static final Album ANOTHER_ALBUM = new Album(2, "Abbey Road", 1969);
    private static final List<Album> ALL_ALBUMS = List.of(EXISTING_ALBUM, ANOTHER_ALBUM);

    @Mock
    private AlbumRepository repository;

    @InjectMocks
    private AlbumServiceImpl service;

    @Test
    void save_returnsIdFromRepository() {
        when(repository.save(any(Album.class))).thenReturn(7);

        int id = service.save("Abbey Road", 1969);

        assertEquals(7, id);
        ArgumentCaptor<Album> captor = ArgumentCaptor.forClass(Album.class);
        verify(repository).save(captor.capture());
        assertEquals("Abbey Road", captor.getValue().getTitle());
        assertEquals(1969, captor.getValue().getReleaseYear());
    }

    @Test
    void findById_returnsAlbumWhenFound() {
        when(repository.findById(1)).thenReturn(EXISTING_ALBUM);

        Album result = service.findById(1);

        assertSame(EXISTING_ALBUM, result);
        verify(repository).findById(1);
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(repository.findById(99)).thenReturn(null);

        assertThrows(AlbumNotFoundException.class, () -> service.findById(99));
        verify(repository).findById(99);
    }

    @Test
    void findByTitle_returnsAlbumWhenFound() {
        when(repository.findByTitle("Abbey Road")).thenReturn(ANOTHER_ALBUM);

        assertSame(ANOTHER_ALBUM, service.findByTitle("Abbey Road"));
        verify(repository).findByTitle("Abbey Road");
    }

    @Test
    void findByTitle_throwsWhenNotFound() {
        when(repository.findByTitle("Unknown")).thenReturn(null);

        assertThrows(AlbumNotFoundException.class, () -> service.findByTitle("Unknown"));
        verify(repository).findByTitle("Unknown");
    }

    @Test
    void findAll_returnsAllAlbums() {
        when(repository.findAll()).thenReturn(ALL_ALBUMS);

        assertEquals(ALL_ALBUMS, service.findAll());
    }

    @Test
    void findAll_returnsEmptyListWhenNothingStored() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void update_appliesChangesWhenFound() {
        Album album = new Album(1, "The Dark Side of the Moon (Remaster)", 1974);
        when(repository.update(album)).thenReturn(true);

        service.update(album);

        verify(repository).update(album);
    }

    @Test
    void update_throwsWhenNotFound() {
        Album album = new Album(99, "Missing", 2000);
        when(repository.update(album)).thenReturn(false);

        assertThrows(AlbumNotFoundException.class, () -> service.update(album));
    }

    @Test
    void deleteById_delegatesToRepository() {
        service.deleteById(2);

        verify(repository).deleteById(2);
    }
}
