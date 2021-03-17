package sandbox.candystore.batch.common.masterdata;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MasterdataLoader {

	@Autowired
	MstProduct mstProduct;

	@Autowired
	MstProductRepository mstProductRepository;

	public void loadMasterdata() {
		loadMstProduct();
	}

	private void loadMstProduct() {
		List<MstProductDto> products = mstProductRepository.select();
		log.info("products.size()=" + products.size());
		List<MstProductDto> list = new ArrayList<MstProductDto>();
		for (MstProductDto product : products) {
			list.add(product);
		}
		mstProduct.init(list);
	}

}
