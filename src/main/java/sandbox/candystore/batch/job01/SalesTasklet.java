package sandbox.candystore.batch.job01;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sandbox.candystore.batch.common.masterdata.MstProduct;
import sandbox.candystore.batch.common.masterdata.MstProductDto;
import sandbox.candystore.batch.common.util.JobItems;

@Component
@Slf4j
public class SalesTasklet implements Tasklet {
	@Autowired
	private ItemStreamReader<OrderDto> orderReader;

	@Autowired
	private ItemWriter<SalesDto> salesWriter;

	@Autowired
	private ItemStreamWriter<OrderDto> errorWriter;

	@Autowired
	private MstProduct mstProduct;

	@Autowired
	private JobItems commonitems;


	private static final int CHUNK_SIZE = 10;

	/**
	 * <pre>
	 * ・インプット
	 * 　注文ファイル：orderReader
	 * 　　商品マスタ：mstProduct
	 *
	 * ・アウトプット
	 * 　売上テーブル：salesWriter
	 * 　　エラーファイル：errorWriter
	 *
	 * 処理概要
	 * 　注文ファイルに書かれている商品を売上テーブルに登録する。
	 * 　　商品の値段は商品マスタから取得する。
	 * 　　　注文ファイルに書かれている商品が商品マスタに存在しない場合、
	 * 　　　その商品は売上テーブルには登録せず、エラーファイルに出力する。
	 * 　　売上日には運用日付を設定する。
	 * 　　　運用日付は共通処理から取得する。
	 *
	 * </pre>
	 *
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("*** *** ***");


		log.info(commonitems.getOpDateStr());

		List<SalesDto> salesItems = new ArrayList<>(CHUNK_SIZE);
		List<OrderDto> errorList = new ArrayList<>(CHUNK_SIZE);
		Map<String, MstProductDto> products = mstProduct.getProductMap();

		try {
			errorWriter.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());
			orderReader.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());

			MstProductDto productItem = null;
			OrderDto orderItem = null;
			while ((orderItem = orderReader.read()) != null) {
				log.info(orderItem.toString());
				String productName = orderItem.getProductName();
				if (products.containsKey(productName)) {
					productItem = products.get(productName);

					SalesDto salesItem = new SalesDto();
					salesItem.setProductName(productItem.getProductName());
					salesItem.setPrice(productItem.getPrice());
					salesItem.setCategory1(productItem.getCategory1());
					salesItem.setCategory2(productItem.getCategory2());
					salesItem.setSalesDate(commonitems.getOpDate());

					salesItems.add( salesItem);
				}else {
					log.info("skipped");
					errorList.add(orderItem);
				}
			}

			salesWriter.write(salesItems);
			// エラーリストをファイル出力
			errorWriter.write(errorList);

		} finally {
			try {
				orderReader.close();
			} catch (ItemStreamException e) {
				// ignore
			}

		}

		return null;
	}

}
