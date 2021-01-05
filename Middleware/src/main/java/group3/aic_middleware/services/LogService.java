package group3.aic_middleware.services;

import group3.aic_middleware.entities.LogEntity;
import group3.aic_middleware.repository.LogRepository;
import group3.aic_middleware.restData.LogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    @Autowired
    LogRepository repository;

    /**
     * Get all logs according to log count
     * @param lastLogsCount get the last logs according to this count (e.g.: 100 would return the last 100 logs)
     */
    public List<LogDTO> getAllLogs(int lastLogsCount) {

        ArrayList<LogDTO> logs = new ArrayList<>();
        Page<LogEntity> temp = repository.findAll(PageRequest.of(0, lastLogsCount));

        for(LogEntity entity : temp.getContent()) {
            logs.add(new LogDTO(entity.getDated(), entity.getLogger(), entity.getLevel(), entity.getMessage()));
        }
        return logs;
    }
}
