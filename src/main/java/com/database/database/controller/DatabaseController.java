package com.database.database.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.database.model.EmptyJSONResponse;
import com.database.database.model.LoginModel;
import com.database.database.model.UserModel;
import com.database.database.repository.UserRepository;

@CrossOrigin
@RestController
@RequestMapping("/api")
@RefreshScope
public class DatabaseController {

	Logger logger = LoggerFactory.getLogger(DatabaseController.class);

	@Autowired
	UserRepository userRepository;

	@Value("${msg:Config Server is not working. Verify configuration properties.}")
	private String msg;

	@PostMapping("/authenticatefromdb")
	public ResponseEntity<UserModel> authenticateUser(@RequestBody LoginModel userModel) {

		logger.info("Entering authenticateUser method in DatabaseController class {}", userModel);

		try {
			List<UserModel> user = userRepository.findByUsername(userModel.getUsername());

			logger.info("Exiting authenticateUser method in DatabaseController class {}", user.get(0));
			if (user.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(user.get(0), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllUsers")
	public ResponseEntity<List<UserModel>> getAllUsers() {

		logger.info("Entering getAllUsers method in DatabaseController class");

		try {
			List<UserModel> tutorials = new ArrayList<UserModel>();
			userRepository.findAll().forEach(tutorials::add);

			logger.info("Exiting getAllUsers method in DatabaseController class {}", tutorials);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<UserModel> getUserById(@PathVariable("id") long id) {

		logger.info("Entering getUserById method in DatabaseController class {}", id);

		Optional<UserModel> user = userRepository.findById(id);

		logger.info("Exiting getUserById method in DatabaseController class {}", user.get());
		if (user.isPresent()) {
			return new ResponseEntity<>(user.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/users")
	public ResponseEntity<String> createUser(@RequestBody UserModel user) {

		logger.info("Entering createUser method in DatabaseController class {}", user);

		try {
			UserModel mod = new UserModel(user.getUsername(), user.getPassword(), user.getFirstName(),
					user.getLastName());
			userRepository.save(mod);
			logger.info("Exiting createUser method in DatabaseController class {}", user);
			return new ResponseEntity<>("created", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<UserModel> updateUser(@PathVariable("id") long id, @RequestBody UserModel user) {

		logger.info("Entering updateUser method in DatabaseController class {} {}", id, user);

		Optional<UserModel> userOptional = userRepository.findById(id);

		logger.info("Entered updateUser method in DatabaseController class {}", userOptional.get(),
				userOptional.isPresent());
		if (userOptional.isPresent()) {
			UserModel userModel = userOptional.get();
			userModel.setUsername(user.getUsername());
			userModel.setPassword(user.getPassword());
			userModel.setFirstName(user.getFirstName());
			userModel.setLastName(user.getLastName());
			userRepository.save(userModel);
			return new ResponseEntity<>(userModel, HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {

		logger.info("Entering deleteUser method in DatabaseController class {}", id);

		try {
			userRepository.deleteById(id);
			logger.info("Exiting deleteUser method in DatabaseController class {}", id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("users/forget")
	public ResponseEntity<Object> forgetPassword(@RequestBody UserModel userModel) {

		logger.info("Entering forgetPassword method in DatabaseController class {}", userModel);

		try {
			List<UserModel> userList = userRepository.findByUsername(userModel.getUsername());
			logger.info("Exiting forgetPassword method in DatabaseController class {}", userList);
			if (!userList.isEmpty()) {
				UserModel user = userList.get(0);
				user.setPassword(user.getFirstName() + "@123");
				userRepository.save(user);
				return new ResponseEntity<>(new EmptyJSONResponse(), HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
