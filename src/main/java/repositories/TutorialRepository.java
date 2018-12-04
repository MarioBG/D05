
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Tutorial;

public interface TutorialRepository extends JpaRepository<Tutorial, Integer> {

	@Query("select hw.tutorials from HandyWorker hw where hw.name = '?1'")
	Collection<Tutorial> findAllHandyWorkerTutorials(String name);

	@Query("select hw.tutorials from HandyWorker hw where hw.userAccount.id = '?1'")
	Collection<Tutorial> findAllHandyWorkerTutorialsFromAccountId(int id);
}
