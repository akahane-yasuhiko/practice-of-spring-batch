package sandbox.candystore.batch.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * ジョブ共通アイテムのアクセサ<br>
 * 設定はジョブ開始イベントで行う
 * @author cbh68
 *
 */
@JobScope
@Component
@Slf4j
public class JobItems {

	private static final String OP_DATE = "OP_DATE_STR";
	private static final String JOB_ID = "JOB_ID";

	private Map<String, Object> jobItems = null;

	public static final SimpleDateFormat dateFormatForOpdate = new SimpleDateFormat("yyyy-MM-dd");

	public void init(Date opDate, String jobId) {
		if(this.jobItems != null) {
			log.warn("jobItems already exist. Ignore it.");
			return;
		}

		Map<String, Object> jobItems = new HashMap<String, Object>();
		jobItems.put(OP_DATE, opDate);
		jobItems.put(JOB_ID, jobId);

		this.jobItems = jobItems;
	}

	public String getOpDateStr() {
		checkIinit();
		Date opDate = (Date)this.jobItems.get(OP_DATE);
		return JobItems.dateFormatForOpdate.format(opDate);
	}

	public Date getOpDate() {
		checkIinit();
		return (Date)this.jobItems.get(OP_DATE);
	}

	public String getJobId() {
		checkIinit();
		return (String)this.jobItems.get(JOB_ID);
	}

	private void checkIinit() {
		if (this.jobItems == null) {
			throw new RuntimeException("Not initialized");
		}
	}
}
