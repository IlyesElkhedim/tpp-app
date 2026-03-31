package back.projet.tpp.repository;

import back.projet.tpp.domain.model.entity.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractTypeRepository extends JpaRepository<ContractType, Integer> {
    Optional<ContractType> findByLabel(String label);
}
