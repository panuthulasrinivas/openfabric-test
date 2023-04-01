package ai.openfabric.api.service;

import ai.openfabric.api.model.RunCmd;
import ai.openfabric.api.model.Stats;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.repository.StatsRepository;
import ai.openfabric.api.repository.WorkerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.InvocationBuilder.AsyncResultCallback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class WorkerService {

  @Autowired
  WorkerRepository workerRepository;
  @Autowired
  StatsRepository statsRepository;
  DockerClient dockerClient;
  @Value("${docker.url}")
  private String dockerUrl;

  @PostConstruct
  public void setUp() {
    dockerClient
        = DockerClientBuilder.getInstance(dockerUrl).build();
  }

  @Transactional
  public void updateContainerInfo() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
    List<String> workerIds = new ArrayList<>();
    List<Worker> workers = new ArrayList<>();
    List<Stats> statsList = new ArrayList<>();
    for (Container container : containers) {
      Worker worker = workerRepository.getByWorkerId(container.getId());
      if (worker == null) {
        worker = new Worker();
        worker.setWorkerId(container.getId());
        worker.setName(container.getNames()[0]);
        worker.setPorts(objectMapper.writeValueAsString(container.getPorts()));
        worker.setImage(container.getImage());
        worker.setImageId(container.getImageId());
        worker.setState(container.getState());
        worker.setStatus(container.getStatus());
        worker.setPorts(objectMapper.writeValueAsString(container.getPorts()));
        worker.setNetworkSettings(objectMapper.writeValueAsString(container.getNetworkSettings()));
        worker.setCommand(container.getCommand());
        worker.setLabels(objectMapper.writeValueAsString(container.getLabels()));
        worker.setMounts(objectMapper.writeValueAsString(container.getMounts()));
        worker.setHostConfig(objectMapper.writeValueAsString(container.getHostConfig()));
      } else {
        worker.setStatus(container.getStatus());
        worker.setMounts(objectMapper.writeValueAsString(container.getMounts()));
        worker.setLabels(objectMapper.writeValueAsString(container.getLabels()));
        worker.setState(container.getState());
        worker.setHostConfig(objectMapper.writeValueAsString(container.getHostConfig()));
        worker.setPorts(objectMapper.writeValueAsString(container.getPorts()));
      }
      workerIds.add(container.getId());
      workers.add(worker);

      AsyncResultCallback<Statistics> callback = new AsyncResultCallback<>();

      dockerClient.statsCmd(containers.get(0).getId()).exec(callback);
      Statistics statistics = callback.awaitResult();
      callback.close();

      Stats stats = statsRepository.getByWorkerId(container.getId());
      if (stats == null) {
        stats = new Stats();
      }
      stats.setWorkerId(container.getId());
      stats.setNetworks(objectMapper.writeValueAsString(statistics.getNetworks()));
      stats.setMemoryStats(objectMapper.writeValueAsString(statistics.getMemoryStats()));
      stats.setBlkioStats(objectMapper.writeValueAsString(statistics.getBlkioStats()));
      stats.setCpuStats(objectMapper.writeValueAsString(statistics.getCpuStats()));
      stats.setNumProcs(objectMapper.writeValueAsString(statistics.getNumProcs()));
      stats.setPrecpuStats(objectMapper.writeValueAsString(statistics.getPreCpuStats()));
      stats.setPidsStats(objectMapper.writeValueAsString(statistics.getPidsStats()));
      stats.setRead(statistics.getRead());
      stats.setPreread(statistics.getPreread());
      statsList.add(stats);
    }

    if (!CollectionUtils.isEmpty(workers)) {
      workerRepository.saveAll(workers);
      statsRepository.saveAll(statsList);
    }

    List<Worker> deletedWorkerList = workerRepository.getByWorkerIdNotIn(workerIds);
    List<Stats> deletedStatsList = statsRepository.getByWorkerIdNotIn(workerIds);

    if (!CollectionUtils.isEmpty(deletedWorkerList)) {
      deletedWorkerList.stream().forEach(w -> {
        w.setStatus("deleted");
        w.deletedAt = new Date();
      });
      workerRepository.saveAll(deletedWorkerList);
    }
    if (!CollectionUtils.isEmpty(deletedStatsList)) {
      deletedStatsList.stream().forEach(s -> {
        s.deletedAt = new Date();
      });
      statsRepository.saveAll(deletedStatsList);
    }
  }


  public Page<Worker> getContainers(int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt"));
    return workerRepository.getAll(pageable);
  }

  public Worker getWorker(String workerId) {
    return workerRepository.getByWorkerId(workerId);
  }

  public Stats getStats(String workerId) {
    return statsRepository.getByWorkerId(workerId);
  }

  public void run(String containerId, RunCmd runCmd) {
    if (runCmd.equals(RunCmd.STOP)) {
      dockerClient.stopContainerCmd(containerId).exec();
    } else {
      dockerClient.startContainerCmd(containerId).exec();
    }
  }


}
