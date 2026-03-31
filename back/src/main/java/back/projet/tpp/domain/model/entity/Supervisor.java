package back.projet.tpp.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "supervisor")
@PrimaryKeyJoinColumn(name = "id")
public class Supervisor extends User {

}

