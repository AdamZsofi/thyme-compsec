package hu.bme.crysys.server.server.controller;

import hu.bme.crysys.server.server.repository.CaffCommentRepository;
import hu.bme.crysys.server.server.repository.CaffFileRepository;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(
        hu.bme.crysys.server.server.controller.CaffController.class);

    private final JdbcTemplate jdbcTemplate;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*@RequestMapping(value = "/userdata", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserData>> getUserData() {
        logger.debug("GET UserData");
        return new ResponseEntity<>(userDataRepository.findAll(), HttpStatus.OK);
    }*/


}
