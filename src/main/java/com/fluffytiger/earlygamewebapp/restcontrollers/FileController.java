package com.fluffytiger.earlygamewebapp.restcontrollers;

import com.fluffytiger.earlygamewebapp.model.JarMeta;
import com.fluffytiger.earlygamewebapp.services.JarStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLConnection;

@RestController
@RequestMapping(value = "api/files")
public class FileController {
    private final JarStorageService files;

    public FileController(JarStorageService files) {
        this.files = files;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ResponseEntity<Iterable<JarMeta>> list() {
        return ResponseEntity.ok(this.files.list());
    }

    @RequestMapping(value = "download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@RequestParam(name = "id") Long id) {
        try {
            Resource resource = this.files.load(id);

            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType(URLConnection.guessContentTypeFromName(resource.getFilename())))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .build();
        }
    }
}
