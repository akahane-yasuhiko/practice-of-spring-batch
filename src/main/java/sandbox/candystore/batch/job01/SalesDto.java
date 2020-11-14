package sandbox.candystore.batch.job01;

import java.util.Date;

import lombok.Data;

@Data
public class SalesDto {
	private String productName;
	private int price;
	private String category1;
	private String category2;
	private Date salesDate;

}
