package sandbox.candystore.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SampleBatchApplication {

	public static void main(String[] args) throws Exception {
		 SpringApplication.run(SampleBatchApplication.class, args);

//		// System.exit is common for Batch applications since the exit code can be used to
//		// drive a workflow
//		System.exit(SpringApplication.exit(SpringApplication.run(
//				SampleBatchApplication.class, args)));
	}
}
