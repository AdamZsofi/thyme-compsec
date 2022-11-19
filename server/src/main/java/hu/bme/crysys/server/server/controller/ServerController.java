package hu.bme.crysys.server.server.controller;

import hu.bme.crysys.server.server.domain.database.UserData;
import hu.bme.crysys.server.server.repository.CaffCommentRepository;
import hu.bme.crysys.server.server.repository.CaffFileRepository;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServerController {

    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private CaffFileRepository caffFileRepository;
    @Autowired
    private CaffCommentRepository caffCommentRepository;
    @Autowired
    private UserDataRepository userDataRepository;

    private final JdbcTemplate jdbcTemplate;

    public ServerController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*@RequestMapping(value = "/userdata", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<UserData>> getUserData() {
        logger.debug("GET UserData");
        return new ResponseEntity<>(userDataRepository.findAll(), HttpStatus.OK);
    }*/
}
