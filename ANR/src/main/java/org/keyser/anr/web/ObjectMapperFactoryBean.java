package org.keyser.anr.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostAction;
import org.keyser.anr.core.CostCredit;
import org.springframework.beans.factory.FactoryBean;

public class ObjectMapperFactoryBean implements FactoryBean<ObjectMapper> {

	/**
	 * Transform les {@link Cost} en chaine
	 * 
	 * @author PAF
	 * 
	 */
	public class CostSerializer extends SerializerBase<Cost> {

		public CostSerializer() {
			super(Cost.class, true);
		}

		@Override
		public void serialize(Cost value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {

			List<String> alls = new ArrayList<>();

			int act = value.sumFor(CostAction.class);
			if (act > 0) {
				String v = "";
				for (int i = 0; i < act; ++i)
					v += "{click}";
				alls.add(v);
			}

			int cred = value.sumFor(CostCredit.class);
			if (cred > 0) {
				alls.add(cred + "{credits}");
			}

			String costLine = alls.stream().collect(Collectors.joining(" + "));
			jgen.writeString(costLine);
		}

	}

	@Override
	public ObjectMapper getObject() throws Exception {
		ObjectMapper om = new ObjectMapper();

		SimpleModule mod = new SimpleModule("ANR", new Version(0, 0, 0, null));
		mod.addSerializer(Cost.class, new CostSerializer());
		om.registerModule(mod);

		SerializationConfig sc = om.getSerializationConfig().withSerializationInclusion(Inclusion.NON_NULL);
		om.setSerializationConfig(sc);
		return om;
	}

	@Override
	public Class<?> getObjectType() {
		return ObjectMapper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
