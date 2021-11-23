package com.scudata.lib.elastic.function;

import com.scudata.common.MessageManager;
import com.scudata.common.RQException;
import com.scudata.dm.Context;
import com.scudata.expression.Function;
import com.scudata.expression.Node;
import com.scudata.resources.EngineMessage;

// es_close(handle)
public class ImClose extends Function {
	
	public Node optimize(Context ctx) {
		return this;
	}

	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("es_close" + mm.getMessage("function.missingParam"));
		}

		Object client = param.getLeafExpression().calculate(ctx);
		if ((client instanceof RestConn)) {
			((RestConn)client).close();
		}else{
			MessageManager mm = EngineMessage.get();
			throw new RQException("es_close" + mm.getMessage("function.paramTypeError"));
		}
		
		return null;
	}
}