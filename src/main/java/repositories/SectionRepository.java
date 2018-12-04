
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Section;

public interface SectionRepository extends JpaRepository<Section, Integer> {

	@Query("select s from Section s order by s.number where s.tutorial.id = ?1")
	public List<Section> getSectionsOrderedFromTutorial(int tutorialId);

}
