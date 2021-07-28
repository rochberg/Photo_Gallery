package com.photogallery.uploadingfiles.storage.services;

import com.photogallery.uploadingfiles.storage.PhotoRepository;
import com.photogallery.uploadingfiles.entities.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DBPhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    public List<Photo> getAll() {
        return photoRepository.findAll();
    }

    public Photo findById(int id){
        return photoRepository.findById(id).get();
    }

    public List<Photo> getPhotosByAlbumId(int albumId) {
        return photoRepository.findPhotosByAlbumId(albumId);
    }

    public List<Integer> getAlbumList() {
        return getAll().parallelStream().map(Photo::getAlbumId).distinct().collect(Collectors.toList());
    }

    public void deleteAll() {
        photoRepository.deleteAll();
    }

    public void save(Photo photo) {
        photoRepository.save(photo);
    }

    public void deletePhotoById(@PathVariable(name = "id") int id) {
        photoRepository.deleteById(id);
    }
}