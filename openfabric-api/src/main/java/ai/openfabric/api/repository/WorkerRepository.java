package ai.openfabric.api.repository;

import ai.openfabric.api.model.Worker;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface WorkerRepository extends CrudRepository<Worker, String> {

  @Query(value = "select w from Worker w where workerId= :workerId")
  Worker getByWorkerId(@Param(value = "workerId") String workerId);

  @Query(value = "select w from Worker w where workerId not in(:workerIds)")
  List<Worker> getByWorkerIdNotIn(@Param(value = "workerIds") List<String> workerIds);

  @Query(value = "select w from Worker w")
  Page<Worker> getAll(Pageable pageable);

}
