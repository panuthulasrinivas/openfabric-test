package ai.openfabric.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.InvocationBuilder.AsyncResultCallback;
import java.io.IOException;
import java.util.List;

public class Test {

  public static void main(String[] args) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    DockerClient dockerClient
        = DockerClientBuilder.getInstance("tcp://localhost:2375").build();
    List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
    System.out.println(objectMapper.writeValueAsString(containers.get(0)));
    AsyncResultCallback<Statistics> callback = new AsyncResultCallback<>();

    dockerClient.statsCmd(containers.get(0).getId()).exec(callback);
    Statistics stats = null;
    try {
      stats = callback.awaitResult();
      callback.close();
    } catch (RuntimeException | IOException e) {
      // you may want to throw an exception here
    }
    System.out.println(objectMapper.writeValueAsString(stats));


  }
}
