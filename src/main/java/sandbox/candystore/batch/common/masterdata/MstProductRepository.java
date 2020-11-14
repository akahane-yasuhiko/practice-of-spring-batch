package sandbox.candystore.batch.common.masterdata;

import java.util.List;

public interface MstProductRepository {
	List<MstProductDto> select();
}
