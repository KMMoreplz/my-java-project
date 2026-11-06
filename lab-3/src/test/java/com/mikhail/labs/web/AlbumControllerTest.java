package com.mikhail.labs.web;

import com.mikhail.labs.model.Album;
import com.mikhail.labs.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MVC-тесты: @SpringBootTest + MockMvc. Замокан репозиторий,
 * сервисный слой и контроллер реальные, база данных не используется.
 */
@SpringBootTest
@ActiveProfiles("test")
class AlbumControllerTest {

    private static final Album EXISTING_ALBUM = new Album(1, "The Dark Side of the Moon", 1973);

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AlbumRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void create_returns201WithCreatedAlbum() throws Exception {
        when(repository.save(any(Album.class))).thenReturn(7);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Abbey Road\",\"releaseYear\":1969}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/albums/7"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Abbey Road"))
                .andExpect(jsonPath("$.releaseYear").value(1969));
    }

    @Test
    void findById_returns200WithAlbum() throws Exception {
        when(repository.findById(1)).thenReturn(EXISTING_ALBUM);

        mockMvc.perform(get("/albums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("The Dark Side of the Moon"))
                .andExpect(jsonPath("$.releaseYear").value(1973));
    }

    @Test
    void findById_returns404WhenNotFound() throws Exception {
        when(repository.findById(99)).thenReturn(null);

        mockMvc.perform(get("/albums/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("id=99")));
    }

    @Test
    void findByTitle_returns200WithAlbum() throws Exception {
        when(repository.findByTitle("Abbey Road")).thenReturn(new Album(2, "Abbey Road", 1969));

        mockMvc.perform(get("/albums/by-title").param("title", "Abbey Road"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Abbey Road"));
    }

    @Test
    void findByTitle_returns404WhenNotFound() throws Exception {
        when(repository.findByTitle("Unknown")).thenReturn(null);

        mockMvc.perform(get("/albums/by-title").param("title", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Unknown")));
    }

    @Test
    void findAll_returns200WithAllAlbums() throws Exception {
        when(repository.findAll()).thenReturn(List.of(EXISTING_ALBUM, new Album(2, "Abbey Road", 1969)));

        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("The Dark Side of the Moon"))
                .andExpect(jsonPath("$[1].title").value("Abbey Road"));
    }

    @Test
    void update_returns204AndPassesIdFromPath() throws Exception {
        when(repository.update(any(Album.class))).thenReturn(true);

        mockMvc.perform(put("/albums/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"The Dark Side of the Moon (Remaster)\",\"releaseYear\":1974}"))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Album> captor = ArgumentCaptor.forClass(Album.class);
        verify(repository).update(captor.capture());
        assertEquals(1, captor.getValue().getId());
        assertEquals("The Dark Side of the Moon (Remaster)", captor.getValue().getTitle());
        assertEquals(1974, captor.getValue().getReleaseYear());
    }

    @Test
    void update_returns404WhenNotFound() throws Exception {
        when(repository.update(any(Album.class))).thenReturn(false);

        mockMvc.perform(put("/albums/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Missing\",\"releaseYear\":2000}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("id=99")));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/albums/1"))
                .andExpect(status().isNoContent());

        verify(repository).deleteById(1);
    }
}
