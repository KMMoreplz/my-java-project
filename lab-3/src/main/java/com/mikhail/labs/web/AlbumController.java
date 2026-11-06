package com.mikhail.labs.web;

import com.mikhail.labs.model.Album;
import com.mikhail.labs.service.AlbumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * REST-контроллер: HTTP-протокол поверх сервисного слоя.
 */
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService service;

    public AlbumController(AlbumService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Album> create(@RequestBody Album album) {
        int id = service.save(album.getTitle(), album.getReleaseYear());
        Album created = new Album(id, album.getTitle(), album.getReleaseYear());
        return ResponseEntity.created(URI.create("/albums/" + id)).body(created);
    }

    @GetMapping("/{id}")
    public Album findById(@PathVariable int id) {
        return service.findById(id);
    }

    @GetMapping("/by-title")
    public Album findByTitle(@RequestParam String title) {
        return service.findByTitle(title);
    }

    @GetMapping
    public List<Album> findAll() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable int id, @RequestBody Album album) {
        service.update(new Album(id, album.getTitle(), album.getReleaseYear()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
