package hu.bme.crysys.server.server.domain.service;

import hu.bme.crysys.server.server.domain.database.CaffComment;
import hu.bme.crysys.server.server.domain.database.CaffFile;
import hu.bme.crysys.server.server.repository.CaffCommentRepository;
import hu.bme.crysys.server.server.repository.CaffFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaffService {
    @Autowired
    private CaffFileRepository caffFileRepository;
    @Autowired
    private CaffCommentRepository caffCommentRepository;

    // TODO dummy
    new CaffFile();
    new CaffFile();


    public static Iterable<CaffFile> getAllProducts() {
        throw new RuntimeException("Not implemented yet");
    }

    public static CaffFile getProduct(long id) {
        throw new RuntimeException("Not implemented yet");
    }

    public static Iterable<CaffComment> getComments(long id) {
        throw new RuntimeException("Not implemented yet");
    }
}
