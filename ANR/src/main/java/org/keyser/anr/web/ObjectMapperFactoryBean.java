package org.keyser.anr.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.beans.factory.FactoryBean;

public class ObjectMapperFactoryBean implements FactoryBean<ObjectMapper> {

	@Override
	public ObjectMapper getObject() throws Exception {
		ObjectMapper om = new ObjectMapper();
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
