package org.keyser.anr.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.keyser.anr.core.Cost;
import org.keyser.anr.core.CostAction;
import org.keyser.anr.core.CostCredit;
import org.keyser.anr.core.runner.BreakCostAnalysis;
import org.springframework.beans.factory.FactoryBean;

public class ObjectMapperFactoryBean implements FactoryBean<ObjectMapper> {

	public static class BreakCostAnalysisSerializer extends SerializerBase<BreakCostAnalysis> {
		public BreakCostAnalysisSerializer() {
			super(BreakCostAnalysis.class, true);
		}

		@Override
		public void serialize(BreakCostAnalysis value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {

			jgen.writeStartObject();

			//jgen.writeNumberField("card", value.getIce().getIce().getId());
			jgen.writeArrayFieldStart("costs");

			JsonSerializer<Object> jsc = provider.findValueSerializer(Cost.class, null);
			for (Entry<Integer, Cost> e : value.entrySet())
				jsc.serialize(e.getValue(), jgen, provider);

			jgen.writeEndArray();
			
			jgen.writeEndObject();

		}
	}

	/**
	 * Transform les {@link Cost} en chaine
	 * 
	 * @author PAF
	 * 
	 */
	public static class CostSerializer extends SerializerBase<Cost> {

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
		mod.addSerializer(new CostSerializer());
		mod.addSerializer(new BreakCostAnalysisSerializer());
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
