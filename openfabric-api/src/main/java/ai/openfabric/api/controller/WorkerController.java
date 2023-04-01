package ai.openfabric.api.controller;

import ai.openfabric.api.model.RunCmd;
import ai.openfabric.api.model.Stats;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.service.WorkerService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {


  @Autowired
  WorkerService workerService;

  @PostMapping(path = "/hello")
  public @ResponseBody
  String hello(@RequestBody String name) {
    return "Hello!" + name;
  }

  @PostMapping(path = "/refresh")
  public ResponseEntity<Void> refresh() throws IOException {
    workerService.updateContainerInfo();
    return ResponseEntity.noContent().build();
  }

  @GetMapping(path = "/get")
  public ResponseEntity<Page<Worker>> getAll(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
    return ResponseEntity.ok(workerService.getContainers(page, pageSize));
  }

  @GetMapping(path = "/get/worker")
  public ResponseEntity<Worker> getWorkerInfo(@RequestParam("workerId") String workerId) {
    return ResponseEntity.ok(workerService.getWorker(workerId));
  }

  @GetMapping(path = "/get/stats")
  public ResponseEntity<Stats> getWorkerStats(@RequestParam("workerId") String workerId) {
    return ResponseEntity.ok(workerService.getStats(workerId));
  }

  @PostMapping(path = "/run")
  public ResponseEntity<Void> run(@RequestParam("workerId") String workerId, @RequestParam("runCmd") RunCmd runCmd) throws IOException {
    workerService.run(workerId, runCmd);
    return ResponseEntity.noContent().build();
  }

}
