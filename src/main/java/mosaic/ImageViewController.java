package mosaic;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ImageViewController {
    @GetMapping("/v/{id}")
    public String index(@PathVariable String id, Model model) {
        model.addAttribute("id", id);
        return "view";
    }
}
