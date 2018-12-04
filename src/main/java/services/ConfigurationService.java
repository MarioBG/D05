
package services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ConfigurationRepository;
import domain.Configuration;

@Service
@Transactional
public class ConfigurationService {

	@Autowired
	private ConfigurationRepository	configurationRepository;


	public Configuration save(final Configuration entity) {
		Assert.notNull(this.configurationRepository.findOne(entity.getId()));
		return this.configurationRepository.save(entity);
	}

	public List<Configuration> findAll() {
		return this.configurationRepository.findAll();
	}

	public Configuration findOne(final Integer id) {
		return this.configurationRepository.findOne(id);
	}

	public boolean exists(final Integer id) {
		return this.configurationRepository.exists(id);
	}

	public void delete(final Configuration entity) {
		throw new IllegalArgumentException();
	}

	public Configuration findConfiguration() {
		return this.configurationRepository.findAll().iterator().next();
	}

}
