package hexlet.code.repository;

import hexlet.code.model.Task;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Optional<Task> findByName(String name);
    boolean existsByAssigneeId(Long id);
    boolean existsByTaskStatusId(Long id);
    @EntityGraph(attributePaths = {"labels", "taskStatus", "assignee"})
    Optional<Task> findWithLabelsById(Long id);
}
