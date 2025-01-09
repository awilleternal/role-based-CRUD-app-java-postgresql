package net.codejava.ProductManager;

import jakarta.validation.Valid;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class AppController {
    @Autowired
    private ProductService service;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserRolesRepository userRolesRepo;
    @RequestMapping("/")
    public String viewHomePage(Model model){
        List<Product>listProducts=service.listAll();
        model.addAttribute("listProducts",listProducts);
        return "index";

    }
    @RequestMapping("/new")
    public String showNewProductPage(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);

        return "new_product";
    }

//    @RequestMapping(value = "/save", method = RequestMethod.POST)
//    public String saveProduct(@ModelAttribute("product") Product product) {
//        service.save(product);
//
//        return "redirect:/";
//    }
    @RequestMapping("/edit/{id}")
    public ModelAndView showEditProductPage(@PathVariable(name = "id") int id) {
        ModelAndView mav = new ModelAndView("edit_product");
        Product product = service.get((long) id);
        mav.addObject("product", product);

        return mav;
    }
    @RequestMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") int id) {
        service.delete((long) id);
        return "redirect:/";
    }
    @RequestMapping ("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup_form";
    }
    @PostMapping("/process_register")
    public String processRegister(User user) {
        // Encode the password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);

        // Save the user
        userRepo.save(user);

        // Assign the default role


        UserRoles userRole = new UserRoles();
        userRole.setUserId(user.getID()); // Retrieve the user's ID after saving
        userRole.setRoleId(1);

        userRolesRepo.save(userRole); // Save the user-role mapping

        return "register_success";
    }
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product, BindingResult result) {
        // Check for validation errors
        if (result.hasErrors()) {
            return "new_product"; // Return to the form view if validation fails
        }

        // Save the product if validation passes
        service.save(product);
        return "redirect:/"; // Redirect to the home page
    }




}
