package com.mikhail.labs.repository;

import com.mikhail.labs.exception.RepositoryException;
import com.mikhail.labs.model.Album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcAlbumRepository implements AlbumRepository {

    private final ConnectionFactory connectionFactory;
    private final String table;

    public JdbcAlbumRepository(ConnectionFactory connectionFactory, String schema, String table) {
        this.connectionFactory = connectionFactory;
        this.table = "\"" + schema + "\".\"" + table + "\"";
    }

    @Override
    public int save(Album album) {
        String sql = "INSERT INTO " + table + " (title, release_year) VALUES (?, ?)";
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, album.getTitle());
            ps.setInt(2, album.getReleaseYear());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    album.setId(id);
                    return id;
                }
            }
            throw new RepositoryException("Не удалось получить id созданного альбома: " + album);
        } catch (SQLException e) {
            throw new RepositoryException("Ошибка при сохранении альбома: " + album, e);
        }
    }

    @Override
    public Album findById(int id) {
        String sql = "SELECT id, title, release_year FROM " + table + " WHERE id = ?";
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Ошибка при поиске альбома по id=" + id, e);
        }
    }

    @Override
    public Album findByTitle(String title) {
        String sql = "SELECT id, title, release_year FROM " + table + " WHERE title = ? ORDER BY id LIMIT 1";
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Ошибка при поиске альбома по названию=" + title, e);
        }
    }

    @Override
    public List<Album> findAll() {
        String sql = "SELECT id, title, release_year FROM " + table + " ORDER BY id";
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            List<Album> albums = new ArrayList<>();
            while (rs.next()) {
                albums.add(map(rs));
            }
            return albums;
        } catch (SQLException e) {
            throw new RepositoryException("Ошибка при получении списка альбомов", e);
        }
    }

    @Override
    public boolean update(Album album) {
        String sql = "UPDATE " + table + " SET title = ?, release_year = ? WHERE id = ?";
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, album.getTitle());
            ps.setInt(2, album.getReleaseYear());
            ps.setInt(3, album.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Ошибка при обновлении альбома: " + album, e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (
                Connection conn = connectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Ошибка при удалении альбома по id=" + id, e);
        }
    }

    private static Album map(ResultSet rs) throws SQLException {
        return new Album(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getInt("release_year")
        );
    }
}
