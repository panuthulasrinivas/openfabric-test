package ai.openfabric.api.repository;

import ai.openfabric.api.model.Stats;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StatsRepository extends CrudRepository<Stats, String> {

  @Query(value = "select s from Stats s where workerId= :workerId")
  Stats getByWorkerId(@Param(value = "workerId") String workerId);

  @Query(value = "select s from Stats s where workerId not in(:workerIds)")
  List<Stats> getByWorkerIdNotIn(@Param(value = "workerIds") List<String> workerIds);

}
