package com.photogallery.uploadingfiles.storage;

import com.photogallery.uploadingfiles.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {

//    @Query("select photo from Photo photo where concat(photo.albumId, '') like  ?1")
    public List<Photo> findPhotosByAlbumId(int albumId);

}
