package org.keyser.anr.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

			Cost a = value.aggregate();

			Map<String, Integer> val = new LinkedHashMap<>();
			val.put("{click}", null);
			val.put("{credits}", null);

			a.getCosts().forEach(cu -> {
				String key = null;
				if (cu instanceof CostCredit)
					key = "{credits}";
				else if (cu instanceof CostAction)
					key = "{click}";

				if (key != null) {
					val.put(key, cu.getValue());
				}
			});

			List<String> alls = new ArrayList<>();
			val.forEach((k, v) -> {
				if (v != null) {
					if (v == 1)
						alls.add(k);
					else if (v > 0)
						alls.add(v + k);
				}
			});
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
