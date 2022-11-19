package hu.bme.crysys.server.server.controller;

import com.sun.istack.NotNull;
import hu.bme.crysys.server.server.domain.database.CaffComment;
import hu.bme.crysys.server.server.domain.service.CaffService;
import hu.bme.crysys.server.server.domain.database.CaffFile;
import hu.bme.crysys.server.server.repository.CaffCommentRepository;
import hu.bme.crysys.server.server.repository.CaffFileRepository;
import hu.bme.crysys.server.server.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CaffController {
    private static final Logger logger = LoggerFactory.getLogger(CaffController.class);

    private final JdbcTemplate jdbcTemplate;

    public CaffController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping(value = { "", "/products" })
    public @NotNull Iterable<CaffFile> getProducts() {
            return CaffService.getAllProducts();
        }

    @GetMapping(value = { "", "/product/{id}" })
    public @NotNull CaffFile getProduct(long id) {
        return CaffService.getProduct(id);
    }

    @GetMapping(value = { "", "/comment/{id}" })
    public @NotNull Iterable<CaffComment> getComments(long id) {
        return CaffService.getComments(id);
    }
}
