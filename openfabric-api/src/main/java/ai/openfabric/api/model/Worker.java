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
public class Worker extends Datable implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
  @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
  public String id;
  private String workerId;
  public String name;
  public String ports;
  public String image;
  public String imageId;
  public String state;
  public String status;
  public String networkSettings;
  public String command;
  public String labels;
  public String mounts;
  public String hostConfig;

}
