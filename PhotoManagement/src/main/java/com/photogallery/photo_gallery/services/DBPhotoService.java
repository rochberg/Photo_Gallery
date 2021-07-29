package com.photogallery.photo_gallery.services;

import com.photogallery.photo_gallery.Photo;
import com.photogallery.photo_gallery.PhotoRepository;
import com.photogallery.photo_gallery.exceptionhandlers.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.MalformedURLException;
import java.nio.file.Path;
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

    public Resource loadAsResource(Path filename) {
        try {
            Resource resource = new UrlResource(filename.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

}