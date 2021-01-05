package group3.aic_middleware.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity()
@Table(name = "logs")
public class LogEntity {

    @Id
    private int id;

    private String dated;

    private String logger;

    private String level;

    private String message;
}
