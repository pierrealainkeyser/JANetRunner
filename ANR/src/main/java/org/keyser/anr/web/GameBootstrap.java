package org.keyser.anr.web;

import java.util.function.Function;

import org.codehaus.jackson.map.ObjectMapper;
import org.keyser.anr.core.CardLocationIce;
import org.keyser.anr.core.Game;
import org.keyser.anr.core.corp.Corp;
import org.keyser.anr.core.corp.nbn.DataRaven;
import org.keyser.anr.core.corp.nbn.MakingNews;
import org.keyser.anr.core.corp.nbn.MatrixAnalyser;
import org.keyser.anr.core.corp.nbn.Tollbooth;
import org.keyser.anr.core.runner.Runner;
import org.springframework.beans.factory.FactoryBean;

public class GameBootstrap implements FactoryBean<Function<String, GameGateway>> {

	private DTOBuilder builder;

	private ObjectMapper mapper;

	public void setBuilder(DTOBuilder builder) {
		this.builder = builder;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public Function<String, GameGateway> getObject() throws Exception {

		Game g = new Game(new Runner(), new MakingNews(), () -> {
		});

		Corp c = g.getCorp();
		c.getRd().add(new Tollbooth());
		c.getRd().add(new DataRaven());
		MatrixAnalyser ma = new MatrixAnalyser();
		c.getRd().add(ma);

		ma.setLocation(new CardLocationIce(c.getArchive(), 1));

		return (s) -> new GameAsDTOGateway(g.setup(), builder, mapper);
	}

	@Override
	public Class<?> getObjectType() {
		return Function.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
