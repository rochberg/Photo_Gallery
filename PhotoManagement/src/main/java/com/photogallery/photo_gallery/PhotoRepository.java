package com.photogallery.photo_gallery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {

//    @Query("select photo from Photo photo where concat(photo.albumId, '') like  ?1")
    public List<Photo> findPhotosByAlbumId(int albumId);

}
