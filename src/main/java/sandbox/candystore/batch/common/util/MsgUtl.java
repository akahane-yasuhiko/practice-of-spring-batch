package sandbox.candystore.batch.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MsgUtl {
	@Autowired
	MessageSource msgSrc;

	public String getMsg(String code, Object... args) {
		return msgSrc.getMessage(code, args, LocaleContextHolder.getLocale());
	}
}
