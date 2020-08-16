package sandbox.candystore.batch.common.listener;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sandbox.candystore.batch.common.masterdata.MasterdataLoader;
import sandbox.candystore.batch.common.masterdata.MstOperationDateRepository;
import sandbox.candystore.batch.common.masterdata.MstProduct;
import sandbox.candystore.batch.common.util.JobItems;
import sandbox.candystore.batch.common.util.MsgUtl;

@Slf4j
@Component
public class JobExecutionListenerImpl implements JobExecutionListener {

	@Autowired
	private MstOperationDateRepository mstDateRepository;

	@Autowired
	private ExitCodeMapper exitCodeMapper;

	@Autowired
	private JobItems jobItems;

	@Autowired
	private MsgUtl msgUtil;

	@Autowired
	private MasterdataLoader masterdataLoader;

	@Autowired
	private MstProduct mstProduct;

	private static final String JOB_PARAM_OPDATE = "opdate";

	/**
	 * ジョブ共通アイテムの設定
	 * マスタの一括読み込み
	 */
	@Override
	public void beforeJob(JobExecution jobExecution) {

		/* ジョブ共通アイテムの設定 */
		// ジョブID取得
		String jobId = jobExecution.getJobInstance().getJobName();

		// 起動パラメータの運用日付を取得
		String jobParamOpdateStr = jobExecution.getJobParameters().getString(JOB_PARAM_OPDATE);
		// 運用日付取得
		Date opDate = getOperationDate(jobParamOpdateStr);

		jobItems.init(opDate, jobId);
		log.info("運用日付：" + jobItems.getOpDateStr());
		log.info("ジョブID：" + jobItems.getJobId());

		/* マスタの一括読み込み */
		masterdataLoader.loadMasterdata();
		log.info("商品数：" + mstProduct.getProductList().size());

	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		// 終了コードの設定
		// 各ステップの一番大きな終了コードをジョブの終了コードにする
		Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
		int exitCode = 0;
		for (StepExecution stepExecution : stepExecutions) {
			int stepExitCoce = exitCodeMapper.intValue(stepExecution.getExitStatus().getExitCode());
			if (exitCode < stepExitCoce) {
				exitCode = stepExitCoce;
				jobExecution.setExitStatus(stepExecution.getExitStatus());
			}
		}
	}

	private Date getOperationDate(String jobParamOpDateStr) {
		// 起動パラメータの運用日付を日付に変換してみる
		Date jobParamOpDate = createOperationDate(jobParamOpDateStr);

		if (jobParamOpDate != null) {
			// 正しく変換された場合はその値を使う
			return jobParamOpDate;
		} else {
			// 起動パラメータで指定なし、もしくは、指定が不正ならDBから取得する
			return mstDateRepository.getOperationDate().getOperationDate();
		}
	}

	/**
	 * 引数の文字列から運用日付を生成する
	 * @param opdateStr:運用日付文字列
	 * @return 運用日付。引数が書式不正、または、実際に存在しない日付の場合にはnullを返す。
	 */
	private Date createOperationDate(String opdateStr) {
		if (null == opdateStr)
			return null;

		Date jobParamOpdate;
		try {
			jobParamOpdate = JobItems.dateFormatForOpdate.parse(opdateStr);
		} catch (ParseException e) {
			log.info(msgUtil.getMsg("lisnters.validation.msg.opdate.parse", opdateStr));
			return null;
		}

		if (opdateStr.equals(JobItems.dateFormatForOpdate.format(jobParamOpdate))) {
			return jobParamOpdate;
		} else {
			log.info(msgUtil.getMsg("lisnters.validation.msg.opdate.invalid", opdateStr));
			return null;
		}
	}

}
