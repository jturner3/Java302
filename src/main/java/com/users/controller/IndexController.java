package com.users.controller;

import static com.users.security.Role.ROLE_ADMIN;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.users.beans.User;
import com.users.beans.UserImage;
import com.users.repositories.UserImageRepository;
import com.users.repositories.UserRepository;
import com.users.security.PermissionService;


@Controller
public class IndexController {
	private static final Logger log = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserImageRepository userImageRepo;
	
	@Autowired
	private PermissionService permissionService;

	@RequestMapping("/greeting")
	public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
		model.addAttribute("name", name);
		model.addAttribute("repoCount", userRepo.count());
		return "greeting";
	}

	@RequestMapping("/")
	public String home(Model model) {
		return permissionService.hasRole(ROLE_ADMIN) ? "redirect:/users" : "redirect:/contacts";	
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping("/users")
	public String listUsers(Model model) {
		model.addAttribute("users", userRepo.findAllByOrderByFirstNameAscLastNameAsc());
		return "listUsers";
	}
	

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
		return new ModelAndView("login", "error", error);
	}
	
	@RequestMapping("/myprofile")
	public String myprofile(Model model) {
		return profile(permissionService.findCurrentUserId(), model);
	}

	@RequestMapping("/user/{userId}")
	public String profile(@PathVariable long userId, Model model) {
		model.addAttribute("user", userRepo.findOne(userId));

		if (!permissionService.canAccessUser(userId)) {
			log.warn("Cannot allow user to view " + userId);
			return "redirect:/";
		}
		
		List<UserImage> images = userImageRepo.findByUserId(userId);
		if (!CollectionUtils.isEmpty(images)) {
			model.addAttribute("userImage", images.get(0));
		}
		model.addAttribute("permissions", permissionService);
		return "profile";
	}

	@RequestMapping(value = "/user/{userId}/edit", method = RequestMethod.GET)
	public String profileEdit(@PathVariable long userId, Model model) {
		model.addAttribute("user", userRepo.findOne(userId));
		
		if (!permissionService.canAccessUser(userId)) {
			log.warn("Cannot allow user to edit " + userId);
			return "profile";
		}
		
		List<UserImage> images = userImageRepo.findByUserId(userId);
		if (!CollectionUtils.isEmpty(images)) {
			model.addAttribute("userImage", images.get(0));
		}
		return "profileEdit";
	}

	@RequestMapping(value = "/user/{userId}/edit", method = RequestMethod.POST)
	public String profileSave(@ModelAttribute User user,
			@PathVariable long userId,
			@RequestParam(name = "removeImage", defaultValue = "false") boolean removeImage,
			@RequestParam("file") MultipartFile file,
			Model model) {
		
		if (!permissionService.canAccessUser(userId)) {
			log.warn("Cannot allow user to edit " + userId);
			return "profile";
			
		}
		
		log.debug("Saving user " + user);
		userRepo.save(user);
		model.addAttribute("message", "User " + user.getEmail() + " saved.");

		if (!file.isEmpty()) {
			try {
				List<UserImage> images = userImageRepo.findByUserId(user.getId());
				UserImage img = (images.size() > 0) ? images.get(0) : new UserImage(userId);
				img.setContentType(file.getContentType());
				img.setImage(file.getBytes());
				userImageRepo.save(img);

				log.debug("Saved Image");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		} else if (removeImage) {
			log.debug("Removing Image");
			// user.setImage(null);
			List<UserImage> images = userImageRepo.findByUserId(user.getId());

			for (UserImage img : images) {
				userImageRepo.delete(img);
			}
		}

		return profile(userId, model);
	}
	//Mapping the way admin can create new users
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/user/create", method = RequestMethod.GET)
	public String createUser(Model model) {
		model.addAttribute("user", new User());

		return "userCreate";
	}
	//Mapping the way the admin post the new users information and saves it.
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/user/create", method = RequestMethod.POST)
	public String createContact(@ModelAttribute User user,
			@RequestParam("file") MultipartFile file, Model model) {

		User savedUser = userRepo.save(user);
		
	return profileSave(savedUser, savedUser.getId(), false, file, model);
	}
}