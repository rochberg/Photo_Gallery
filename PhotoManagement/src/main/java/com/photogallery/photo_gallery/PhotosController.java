package com.photogallery.photo_gallery;

import com.photogallery.photo_gallery.services.DBPhotoService;
import com.photogallery.photo_gallery.services.InitPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
        /* initialize database with photo-objects from given url */

        deleteAll();
        Photo[] photos = initPhotoService.init();
        Arrays.stream(photos).forEach(this::save);
        return "redirect:/index";
    }


    @RequestMapping("/index")
    public String listPhotos(Model model) {
        /* show photo parameters stored in database */
        List<Photo> p = dbPhotoService.getAll().stream().map(initPhotoService::changePath).collect(Collectors.toList());
//        model.addAttribute("photos", dbPhotoService.getAll().stream().map(initPhotoService::changePath));
        model.addAttribute("photos", p);
        List<Integer> album_options = dbPhotoService.getAlbumList();
        model.addAttribute("album_options", album_options);
        return "index";
    }

    @ResponseBody
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        /* return content of saved file */

        Resource file = dbPhotoService.loadAsResource(initPhotoService.resolvePath(filename));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping("/deleteAll")
    public String deleteAll() {
        /* delete all photos from repository */

        dbPhotoService.deleteAll();
        FileSystemUtils.deleteRecursively(Path.of(initPhotoService.dirPath()).toFile());
        return "index";
    }

    @RequestMapping("/filter_by_album")
    public String filterByAlbum(@RequestParam("albumId") int albumId, Model model) {
        /* return only photos from specific album */

        model.addAttribute("photosByAlbum", dbPhotoService.getPhotosByAlbumId(albumId));
        model.addAttribute("albumId", albumId);
        return "filter_by_album";
    }


    @RequestMapping("/new")
    public String showNewPhotoPage(Model model) {
        /* add a new photo to database */

        Photo photo = new Photo();
        model.addAttribute("photo", photo);
        return "new_photo";
    }

    @RequestMapping(value = "/save_new", method = RequestMethod.POST)
    public String saveNewPhoto(@ModelAttribute("photo") Photo photo) {
//        String path = initPhotoService.dirPath();
        photo.setPath(initPhotoService.resolvePath("/photo" + photo.getId() + ".jpg"));
        initPhotoService.addAttributes(photo);
        return save(photo);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("photo") Photo photo) {
        /* save to database */

        dbPhotoService.save(photo);
        return "redirect:/index";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditPhotoPage(@PathVariable(name = "id") int id) {
        /* return View with a specific photo mapped for edit procedure */

        ModelAndView mav = new ModelAndView("edit_photo");
        Photo photo = dbPhotoService.findById(id);
        mav.addObject("photo", photo);

        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deletePhoto(@PathVariable(name = "id") int id) {
        /* delete photo from repo */

        dbPhotoService.deletePhotoById(id);
        return "redirect:/index";
    }

}
