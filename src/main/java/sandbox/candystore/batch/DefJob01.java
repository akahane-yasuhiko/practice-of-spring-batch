package sandbox.candystore.batch;


import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import sandbox.candystore.batch.job01.OrderDto;
import sandbox.candystore.batch.job01.SalesDto;

/**
 * ジョブ1
 * インプットファイルを読み込んで売上テーブルにINSERT
 *   値段は商品マスタから取得
 */
@Configuration
@MapperScan("sandbox.candystore.batch")
@EnableBatchProcessing
public class DefJob01 {

	/* FW提供のコンポーネント */
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	/* 自作のコンポーネント */
	@Autowired
	private JobParametersIncrementer simpleJobParametersIncrementer;

	@Autowired
	private JobExecutionListener JobExecutionListenerImpl;

	@Autowired
	private Tasklet salesTasklet;

	/* ジョブ */
	@Bean
	public Job job01() throws Exception {
		return this.jobs.get("job01")
				.incrementer(simpleJobParametersIncrementer)
				.listener(JobExecutionListenerImpl)
				.start(step01())
				.build();
	}

	/* ステップ */
	@Bean
	protected Step step01() throws Exception {
		return this.steps.get("step01").tasklet(salesTasklet).build();
	}


	/* リーダー、ライター */
	@StepScope
	@Bean
	public FlatFileItemReader<OrderDto> orderReader(@Value("#{jobParameters['input.file']}") String name) {
		return new FlatFileItemReaderBuilder<OrderDto>()
				.name("orderReader")
				.resource(new FileSystemResource(name))
				.delimited()
				.names(new String[] { "productName" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<OrderDto>() {
					/** 無名クラスのコンストラクタ ターゲットとするDTOを指定*/
					{
						setTargetType(OrderDto.class);
					}
				})
				.build();
	}

	@StepScope
    @Bean
    public MyBatisBatchItemWriter<SalesDto> salesWriter(){
    	return new MyBatisBatchItemWriterBuilder<SalesDto>()
    			.sqlSessionFactory(sqlSessionFactory)
    			.statementId("sandbox.candystore.batch.job01.SalesRepository.insertSales")
    			.build();
    }

//	// 参考用　DBからのリーダー
//	@StepScope
//    @Bean
//    public MyBatisCursorItemReader<MstProductDto> productReader(){
//    	return new MyBatisCursorItemReaderBuilder<MstProductDto>()
//    			.sqlSessionFactory(sqlSessionFactory)
//    			.queryId("sandbox.candystore.batch.common.masterdata.MstProductRepository.select")
//    			.build();
//    }


	@StepScope
	@Bean
	public FlatFileItemWriter<OrderDto> errorWriter(@Value("#{jobParameters['error.file']}") String name) {
		return new FlatFileItemWriterBuilder<OrderDto>()
				.resource(new FileSystemResource(name))
				.name("errorWriter")
//				// 参考用。自由なフォーマットで出力したければアグリゲーターを自分で定義。
//				.lineAggregator(new LineAggregator<OrderDto>() {
//
//					@Override
//					public String aggregate(OrderDto item) {
//						// DTOを一行の文字列にして返却する
//						return item.getProductName();
//					}
//				})
				// 単純なフォーマットでよければデリミタを使用すると指定して対象フィードを指定すればOK。
				.delimited().names("productName")
				.build();
	}

}
