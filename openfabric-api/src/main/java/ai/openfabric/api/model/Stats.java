package ai.openfabric.api.model;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity()
@Data
public class Stats extends Datable implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
  @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
  private String id;

  private String workerId;
  private String networks;
  private String memoryStats;
  private String blkioStats;
  private String cpuStats;
  private String numProcs;
  private String precpuStats;
  private String pidsStats;
  private String read;
  private String preread;

}
