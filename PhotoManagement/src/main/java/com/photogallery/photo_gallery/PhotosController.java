package com.photogallery.photo_gallery;

import com.photogallery.photo_gallery.services.DBPhotoService;
import com.photogallery.photo_gallery.services.InitPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;


@Controller
public class PhotosController {

    @Autowired
    private InitPhotoService initPhotoService;

    @Autowired
    private DBPhotoService dbPhotoService;

    public PhotosController() {
    }


    @RequestMapping("/")
    public String init() {
        //initialize database with photo-objects from given url
        deleteAll();
        Photo[] photos = initPhotoService.init();
        Arrays.stream(photos).forEach(this::save);
        return "redirect:/index";
    }


    @RequestMapping("/index")
    public String listPhotos(Model model) {
        //show photo parameters stored in database
        model.addAttribute("photos", dbPhotoService.getAll());
        List<Integer> album_options = dbPhotoService.getAlbumList();
        model.addAttribute("album_options", album_options);
        return "index";
    }

    @ResponseBody
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        //

        Resource file = dbPhotoService.loadAsResource(initPhotoService.load(filename));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping("/deleteAll")
    public String deleteAll() {
        dbPhotoService.deleteAll();
        return "index";
    }

    @RequestMapping("/filter_by_album")
    public String filterByAlbum(@RequestParam("albumId") int albumId, Model model) {
        model.addAttribute("photosByAlbum", dbPhotoService.getPhotosByAlbumId(albumId));
        model.addAttribute("albumId", albumId);
        return "filter_by_album";
    }


    @RequestMapping("/new")
    public String showNewPhotoPage(Model model) {
        Photo photo = new Photo();
        model.addAttribute("photo", photo);
        return "new_photo";
    }

    @RequestMapping(value = "/save_new", method = RequestMethod.POST)
    public String saveNewPhoto(@ModelAttribute("photo") Photo photo) {
        String path = initPhotoService.dirPath();
        photo.setPath(path + "/photo" + photo.getId() + ".jpg");
        initPhotoService.addAttributes(photo);
        return save(photo);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("photo") Photo photo) {
        dbPhotoService.save(photo);
        return "redirect:/index";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditPhotoPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("edit_photo");
        Photo photo = dbPhotoService.findById(id);
        mav.addObject("photo", photo);

        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deletePhoto(@PathVariable(name = "id") int id) {
        dbPhotoService.deletePhotoById(id);
        return "redirect:/index";
    }

    @RequestMapping("/download/{id}")
    public void downloadPhotoById(@PathVariable(name = "id") int id) {

    }


}
