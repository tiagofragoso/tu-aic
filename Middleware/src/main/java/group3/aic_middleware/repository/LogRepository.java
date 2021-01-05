package group3.aic_middleware.repository;

import group3.aic_middleware.entities.LogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LogRepository extends CrudRepository<LogEntity, String> {

    Page<LogEntity> findAll(Pageable pageable);
}
