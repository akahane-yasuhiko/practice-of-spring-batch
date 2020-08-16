package sandbox.candystore.batch.common.masterdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sandbox.candystore.batch.common.util.MsgUtl;

@Component
public class MstProduct {
	private List<MstProductDto> productList = null;
	private Map<String,MstProductDto> productMap = null;

	@Autowired
	private MsgUtl msgUtill;

	public void init(List<MstProductDto> products) {
		this.productList = products;

		Map<String, MstProductDto> productMap = new HashMap<String, MstProductDto>();
		for(MstProductDto product: products) {
			productMap.put(product.getProductName(), product);
		}
		this.productMap = productMap;
	}

	public List<MstProductDto> getProductList(){
		checkInit();
		return this.productList;
	}

	public Map<String,MstProductDto> getProductMap(){
		checkInit();
		return this.productMap;
	}

	private void checkInit() {
		if(this.productList == null) {
			throw new IllegalStateException(msgUtill.getMsg("masterdata.validation.null"));
		}
	}


}
