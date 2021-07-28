package com.photogallery.photo_gallery.services;

import com.fasterxml.jackson.databind.ObjectMapper;
//import main.java.com.photogallery.photo_gallery.ExecuteOnce;
//import main.java.com.photogallery.photo_gallery.Photo;
//import main.java.com.photogallery.photo_gallery.PhotoRepository;
//import main.java.com.photogallery.photo_gallery.PhotosController;
import com.photogallery.photo_gallery.ExecuteOnce;
import com.photogallery.photo_gallery.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Component
@Transactional
public class InitPhotoService {

    private Photo[] photos;
    private final ExecuteOnce once;


    @Value("${photosDir}")
    private String photosDir;

    public InitPhotoService(){
        once = new ExecuteOnce();
    }

    public Photo[] init(){
        once.run(new Runnable() {
            @Override
            public void run() {
                try {
                    downloadAll();
                } catch (Exception e) {
                }
            }
        });
        return photos;
    }

    private void downloadAll() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject("https://shield-j-test.s3.amazonaws.com/photo.txt", String.class);
        try {
            photos = mapper.readValue(json, Photo[].class);
            downloadPhotos();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadPhotos() throws IOException {
        String path = dirPath();
        for (Photo photo : photos) {
            photo.setPath(path + "/photo" + photo.getId() + ".jpg");
            savePhotoFromURL(photo);
        }
    }

    public String dirPath() {
        File pathAsFile = new File(photosDir);
        if (!Files.exists(Paths.get(photosDir))) {
            pathAsFile.mkdir();
        }
        return photosDir;
    }

    public void savePhotoFromURL(Photo photo) throws IOException {
        URL imageUrl = new URL(photo.getUrl());
        InputStream inputStream = imageUrl.openStream();
        OutputStream outputStream = new FileOutputStream(photo.getPathString());

        byte[] byteArray = new byte[2048];
        int length;

        while ((length = inputStream.read(byteArray)) != -1) {
            outputStream.write(byteArray, 0, length);
        }
        inputStream.close();
        outputStream.close();
        addAttributes(photo);
    }

    public void addAttributes(Photo photo) {
        photo.setDownloadedDate();
        File f = new File(photo.getPathString());
        photo.setSize(f.length());
        photo.setPath(Path.of(System.getProperty("user.dir")).relativize(photo.getPath()));
    }
}