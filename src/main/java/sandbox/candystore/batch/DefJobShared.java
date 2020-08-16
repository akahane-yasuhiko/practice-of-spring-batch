package sandbox.candystore.batch;

import java.util.Map;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sandbox.candystore.batch.common.util.JobStatusEnum;

@Configuration
public class DefJobShared {
	@Bean
	public ExitCodeMapper exitCodeMapper() {
		SimpleJvmExitCodeMapper mapper = new SimpleJvmExitCodeMapper();
		Map<String, Integer> exitCodeMap = mapper.getMapping();
		exitCodeMap.put(JobStatusEnum.WARN101.name(), 101);
		exitCodeMap.put(JobStatusEnum.WARN102.name(), 102);
		exitCodeMap.put(JobStatusEnum.ERR_201.name(), 201);
		exitCodeMap.put(JobStatusEnum.ERR_202.name(), 202);
		mapper.setMapping(exitCodeMap);

		return mapper;

	}

	@Bean
	public JobParametersIncrementer SimpleJobParametersIncrementer() {
		JobParametersIncrementer incrementer = new JobParametersIncrementer() {
			@Override
			public JobParameters getNext(JobParameters parameters) {
				return new JobParametersBuilder().addLong("run.id", System.currentTimeMillis()).toJobParameters();
			}
		};

		return incrementer;
	}
}
