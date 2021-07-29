package com.photogallery.photo_gallery.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.photogallery.photo_gallery.ExecuteOnce;
import com.photogallery.photo_gallery.Photo;
import com.photogallery.photo_gallery.PhotosController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
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

    @Value("${resourcesDirPath}")
    private String resourcesDirPath;

    @Value("${photos.dir}")
    private String photos_dir;


    public InitPhotoService(){
        once = new ExecuteOnce();
    }

    public Photo[] init(){
        /* makes sure you execute initialization once */

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
        dirPath();
        for (Photo photo : photos) {
            photo.setPath(photos_dir+"/photo" +photo.getId() + ".jpg");
            System.out.println("sownlo:\t"+photo.getPathString());
//            photo.setPath(path + "/photo"+photo.getId()+".jpg");
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
        ReadableByteChannel readableByteChannel = Channels.newChannel(imageUrl.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(photo.getPathString());
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileOutputStream.getChannel()
                .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

//        InputStream inputStream = imageUrl.openStream();
//        OutputStream outputStream = new FileOutputStream(photo.getPathString());
//
//        byte[] byteArray = new byte[2048];
//        int length;
//
//        while ((length = inputStream.read(byteArray)) != -1) {
//            outputStream.write(byteArray, 0, length);
//        }
//        inputStream.close();
//        outputStream.close();
        addAttributes(photo);
    }

    public void addAttributes(Photo photo) {
        photo.setDownloadedDate();
        File f = new File(photo.getPathString());
        photo.setSize(f.length());

        /* for later downloading use (to client side) */
        String serveFile = MvcUriComponentsBuilder.fromMethodName(PhotosController.class,
                "serveFile", photo.getPath().getFileName().toString()).build().toUri().toString();
        photo.setDownloadPath(serveFile.toString());
//        System.out.println(Path.of(photos_dir).resolve(relativizePath(photo.getPathString())));
//        photo.setPath(Path.of(photos_dir).resolve(relativizePath(photo.getPathString())));

    }

    public Path resolvePath(String filename) {
        return Paths.get(photosDir).resolve(filename);
    }

    public Photo changePath(Photo photo){
        System.out.println("dir:\t"+photos_dir);
        System.out.println("photo:\t"+photo.getPathString());
//        System.out.println(Path.of(photos_dir).resolve(Path.of(photo.getPathString())).toString());
//        photo.setPath(Path.of(photos_dir).resolve(relativizePath(photo.getPathString())));
        return photo;
    }


    public Path relativizePath(String filename){

        /*System.out.println(Path.of(resourcesDirPath).relativize(Path.of(filename))); */

        Path p = Path.of(photosDir).relativize(Path.of(filename));
        System.out.println("relatavize:  "+p);
//        return  Path.of(filename).relativize(Paths.get(photosDir)).normalize();
//        return  Path.of(resourcesDirPath).relativize(Path.of(filename));
        return p;
    }

}