
package services;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.HandyWorkerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Application;
import domain.Box;
import domain.Customer;
import domain.Endorsement;
import domain.FixUpTask;
import domain.HandyWorker;
import domain.Phase;
import domain.SocialIdentity;
import domain.Tutorial;

@Service
@Transactional
public class HandyWorkerService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private HandyWorkerRepository	handyWorkerRepository;

	@Autowired
	private CustomerService			customerService;

	@Autowired
	private FixUpTaskService		fixUpTaskService;

	@Autowired
	private ApplicationService		applicationService;


	// Supporting services ----------------------------------------------------

	// Simple CRUD methods ----------------------------------------------------

	public Collection<HandyWorker> findAll() {
		Collection<HandyWorker> result;

		result = this.handyWorkerRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public boolean exists(final Integer arg0) {
		return this.handyWorkerRepository.exists(arg0);
	}

	public HandyWorker findOne(final int handyWorkerId) {
		Assert.isTrue(handyWorkerId != 0);

		HandyWorker result;

		result = this.handyWorkerRepository.findOne(handyWorkerId);
		Assert.notNull(result);

		return result;
	}

	public HandyWorker save(final HandyWorker handyWorker) {
		HandyWorker result, saved;
		final UserAccount logedUserAccount;
		Authority authority;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		authority.setAuthority("HANDYWORKER");
		Assert.notNull(handyWorker, "handyWorker.not.null");

		if (this.exists(handyWorker.getId())) {
			logedUserAccount = LoginService.getPrincipal();
			Assert.notNull(logedUserAccount, "handyWorker.notLogged ");
			Assert.isTrue(logedUserAccount.equals(handyWorker.getUserAccount()), "handyWorker.notEqual.userAccount");
			saved = this.handyWorkerRepository.findOne(handyWorker.getId());
			Assert.notNull(saved, "handyWorker.not.null");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(handyWorker.getUserAccount().getUsername()), "handyWorker.notEqual.username");
			Assert.isTrue(handyWorker.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "handyWorker.notEqual.password");
			Assert.isTrue(handyWorker.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && handyWorker.isSuspicious() == saved.isSuspicious(), "handyWorker.notEqual.accountOrSuspicious");

		} else {
			Assert.isTrue(handyWorker.isSuspicious() == false, "handyWorker.notSuspicious.false");
			handyWorker.getUserAccount().setPassword(encoder.encodePassword(handyWorker.getUserAccount().getPassword(), null));
			handyWorker.getUserAccount().setEnabled(true);

		}

		result = this.handyWorkerRepository.save(handyWorker);

		return result;

	}

	public void addToHandyWorkerEndorsements(final HandyWorker handyWorker, final Endorsement e) {
		final Authority authority = new Authority();
		authority.setAuthority(Authority.CUSTOMER);
		Assert.notNull(handyWorker, "handy.worker.not.null");
		Assert.notNull(e, "handy.worker.endorsement.not.null");
		final UserAccount logedUserAccount = LoginService.getPrincipal();
		Assert.notNull(logedUserAccount, "handy.worker.notLogged");
		Assert.isTrue(logedUserAccount.getAuthorities().contains(authority));
		final Customer customer = this.customerService.findByUserAccountId(e.getCustomer().getUserAccount().getId());
		Assert.isTrue(this.customerService.findByHandyWorkerUserAccountId(handyWorker.getUserAccount().getId()).contains(customer));
		final Collection<Endorsement> endorsements = handyWorker.getEndorsements();
		endorsements.add(e);
		handyWorker.setEndorsements(endorsements);
		this.handyWorkerRepository.save(handyWorker);
	}

	public HandyWorker create() {
		HandyWorker result;
		UserAccount userAccount;
		Authority authority;

		result = new HandyWorker();
		userAccount = new UserAccount();
		authority = new Authority();

		result.setSuspicious(false);

		authority.setAuthority("HANDYWORKER");
		userAccount.addAuthority(authority);
		userAccount.setEnabled(true);

		final Collection<Application> applications = new LinkedList<>();
		result.setApplications(applications);
		final Collection<Box> boxes = new LinkedList<>();
		result.setBoxes(boxes);
		final Collection<Endorsement> endorsements = new LinkedList<>();
		result.setEndorsements(endorsements);
		final Collection<SocialIdentity> socialIdentity = new LinkedList<>();
		result.setSocialIdentity(socialIdentity);
		final Collection<Tutorial> tutorials = new LinkedList<>();
		result.setTutorials(tutorials);
		result.setUserAccount(userAccount);

		return result;
	}

	public void delete(final HandyWorker handyWorker) {
		Assert.notNull(handyWorker);
		Assert.isTrue(handyWorker.getId() != 0);

		this.handyWorkerRepository.delete(handyWorker);
	}

	public HandyWorker findHandyWorkerByUserAccount(final UserAccount userAccount) {
		HandyWorker result;

		Assert.isTrue(userAccount.getId() != 0);

		result = this.handyWorkerRepository.findByUserAccountId(userAccount.getId());

		return result;
	}

	public HandyWorker findByApplication(final Application application) {
		HandyWorker result;

		Assert.notNull(application);
		Assert.isTrue(application.getId() != 0);

		result = this.handyWorkerRepository.findByApplicationId(application.getId());

		return result;
	}

	public HandyWorker findByFixUpTask(final FixUpTask fixUpTask) {
		HandyWorker result;
		Assert.notNull(fixUpTask);
		Assert.isTrue(fixUpTask.getId() != 0);
		result = this.handyWorkerRepository.findByFixUpTaskId(fixUpTask.getId());
		return result;
	}

	public Collection<HandyWorker> findByCustomerUserAccountId(final int id) {
		return this.handyWorkerRepository.handyWorkersWorkedForCustomerWithUserAccountId(id);
	}

	public Customer findCustomerProfile(final FixUpTask fixUpTask) {
		Customer res;
		Assert.notNull(fixUpTask);
		res = this.customerService.findCustomerByFixUpTask(fixUpTask);
		Assert.notNull(res);
		return res;
	}

	public Collection<FixUpTask> allCustomerFixUpTask(final Customer customer) {
		Collection<FixUpTask> res = new LinkedList<>();
		Assert.notNull(customer);
		res = this.fixUpTaskService.findFixUpTasksByCustomer(customer);
		Assert.notNull(res);
		return res;
	}

	public HandyWorker findByPrincipal() {
		HandyWorker res;
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		if (userAccount == null)
			res = null;
		else
			res = this.handyWorkerRepository.findByUserAccountId(userAccount.getId());
		return res;
	}

	public HandyWorker findHandyWorkerByApplication(final Application application) {
		HandyWorker res;
		Assert.notNull(application);
		res = this.handyWorkerRepository.findByApplicationId(application.getId());
		Assert.notNull(res);
		return res;
	}

	public FixUpTask saveHandyWorkerFixUpTask(final FixUpTask fixUpTask, final Collection<Phase> phases) {
		FixUpTask result, saved;
		final UserAccount logedUserAccount;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("HANDYWORKER");
		Assert.notNull(fixUpTask, "fixUpTask.not.null");
		final HandyWorker handyWorker = this.findByFixUpTask(fixUpTask);

		if (this.exists(fixUpTask.getId()) && this.applicationService.findAcceptedHandyWorkerApplicationByFixUpTask(fixUpTask).getStatus().equals("ACCEPTED")) {
			logedUserAccount = LoginService.getPrincipal();
			Assert.notNull(logedUserAccount, "handyWorker.notLogged ");
			Assert.isTrue(logedUserAccount.equals(handyWorker.getUserAccount()), "handyWorker.notEqual.userAccount");
			saved = this.fixUpTaskService.findOne(fixUpTask.getId());
			Assert.notNull(saved, "fixUpTask.not.null");
			Assert.isTrue(handyWorker.getUserAccount().isAccountNonLocked() && !(handyWorker.isSuspicious()), "customer.notEqual.accountOrSuspicious");
			if (!phases.isEmpty())
				fixUpTask.getPhases().addAll(phases);
			result = this.fixUpTaskService.save(fixUpTask);
			Assert.notNull(result);
			return result;
		} else {
			result = this.fixUpTaskService.findOne(fixUpTask.getId());
			return result;
		}
	}

	public Application saveHandyWorkerApplication(final Application application, final String comment) {
		final Application result, saved;
		Assert.notNull(application);
		Assert.isTrue(application.getId() != 0);
		final UserAccount userAccount = LoginService.getPrincipal();
		final Date currentMoment = new Date(System.currentTimeMillis() - 1);
		final Authority authority;
		final UserAccount logedUserAccount;
		authority = new Authority();
		authority.setAuthority("HANDYWORKER");

		if (this.exists(application.getId()) && application.getStatus().equals("PENDING") && userAccount.getAuthorities().contains(authority)
			&& this.applicationService.findApplicationsByCustomer(this.customerService.findCustomerByApplication(application)).contains(application)) {
			logedUserAccount = LoginService.getPrincipal();
			Assert.notNull(logedUserAccount, "customer.notLogged ");
			Assert.isTrue(logedUserAccount.equals(this.customerService.findCustomerByApplication(application).getUserAccount()), "handyWorker.notEqual.userAccount");
			if (application.getApplicationMoment().compareTo(currentMoment) < 0) {
				saved = this.applicationService.findOne(application.getId());
				Assert.notNull(saved, "application.not.null");
				if (!comment.equals(null))
					application.getComments().add(logedUserAccount.getUsername() + ": - " + comment);
				result = this.applicationService.save(application);
				return result;
			} else {
				saved = this.applicationService.findOne(application.getId());
				Assert.notNull(saved, "application.not.null");
				if (!comment.equals(null))
					application.getComments().add(logedUserAccount.getUsername() + ": - " + comment);
				application.setStatus("ACCEPTED");
				result = this.applicationService.save(application);
				return result;
			}
		} else {

			result = this.applicationService.save(application);
			return result;
		}
	}

	public HandyWorker findByUserAccountId(final int id) {
		return this.handyWorkerRepository.findByUserAccountId(id);
	}

}
