package com.photogallery.photo_gallery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.util.SimpleIdGenerator;

import javax.persistence.*;
//import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo {


    private int albumId;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    private int id;

    private String title;
    private String url;
    private String thumbnailUrl;
    private Date downloadDate;
    private Long size;
    private String path;

    public Photo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String toString() {
        return this.url;
    }

//    public AlbumNum getAlbumId() {
//        return albumId;
//    }
//
//    public void setAlbumId(int albumId) {
//        this.albumId = new AlbumNum(albumId);
//    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Path getPath() {
        return Path.of(path);
    }

    public String getPathString(){
        return path.toString();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPath(Path path) {
        this.path = path.toString();
    }

    public Date getDownloadedDate() {
        return downloadDate;
    }

    public void setDownloadedDate() {
        this.downloadDate = new Date();
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }


}