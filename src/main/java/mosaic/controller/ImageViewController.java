package mosaic.controller;

import mosaic.data.ImageStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;

@Controller
public class ImageViewController {
    private final ImageStore imageStore;

    @Autowired
    public ImageViewController(@Qualifier(value = "userStore") ImageStore imageStore) {
        this.imageStore = imageStore;
    }

    @GetMapping("/v/{id}")
    public String index(@PathVariable String id, Model model) throws IOException {
        File file = imageStore.get(id);

        model.addAttribute("imgFile", file.getName());
        return "view";
    }
}
